package com.davistsin.bluetoothle.replicas;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * BLE从机
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class BleGattServer {
    private static final String TAG = BleGattServer.class.getSimpleName();
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private final List<BluetoothDevice> mConnectDevices = new ArrayList<>();

    private BluetoothGattServer mBluetoothGattServer;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private Set<OnAdvertiseListener> mOnAdvertiseListeners = new CopyOnWriteArraySet<>();
    private Set<OnConnectionStateChangeListener> mOnConnectionStateChangeListeners = new CopyOnWriteArraySet<>();
    private Set<OnServiceAddedListener> mOnServiceAddedListeners = new CopyOnWriteArraySet<>();
    private Set<OnWriteRequestListener> mOnWriteRequestListeners = new CopyOnWriteArraySet<>();
    private Set<OnReadRequestListener> mOnReadRequestListeners = new CopyOnWriteArraySet<>();

    private final AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(final AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);

            for (OnAdvertiseListener listener : mOnAdvertiseListeners) {
                listener.onStartSuccess(settingsInEffect);
            }

        }

        @Override
        public void onStartFailure(final int errorCode) {
            super.onStartFailure(errorCode);

            for (OnAdvertiseListener listener : mOnAdvertiseListeners) {
                listener.onStartFailure(errorCode);
            }

        }
    };

    private final BluetoothGattServerCallback mBluetoothGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothDevice device, final int status, final int newState) {
            super.onConnectionStateChange(device, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectDevices.add(device);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectDevices.remove(device);
            }

            for (OnConnectionStateChangeListener listener : mOnConnectionStateChangeListeners) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    listener.onConnected(device);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    listener.onDisconnected(device);
                }
                listener.onChange(device, status, newState);
            }

        }

        @Override
        public void onServiceAdded(final int status, final BluetoothGattService service) {
            super.onServiceAdded(status, service);

            for (OnServiceAddedListener listener : mOnServiceAddedListeners) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    listener.onSuccess(service);
                } else {
                    listener.onFail(service);
                }
            }

        }

        @Override
        public void onCharacteristicReadRequest(final BluetoothDevice device, int requestId, int offset, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.d(TAG, "onCharacteristicReadRequest : " + Arrays.toString(characteristic.getValue()));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, characteristic.getValue());

            for (OnReadRequestListener listener : mOnReadRequestListeners) {
                listener.onCharacteristicRead(device, characteristic);
            }
        }

        @Override
        public void onCharacteristicWriteRequest(final BluetoothDevice device, int requestId, final BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, final byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            Log.d(TAG, "onCharacteristicWriteRequest : " + Arrays.toString(value));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);

            for (OnWriteRequestListener listener : mOnWriteRequestListeners) {
                listener.onCharacteristicWritten(device, characteristic, value);
            }
        }

        @Override
        public void onDescriptorReadRequest(final BluetoothDevice device, int requestId, int offset, final BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            Log.d(TAG, "onDescriptorReadRequest : " + Arrays.toString(descriptor.getValue()));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, descriptor.getValue());

            for (OnReadRequestListener listener : mOnReadRequestListeners) {
                listener.onDescriptorRead(device, descriptor);
            }
        }

        @Override
        public void onDescriptorWriteRequest(final BluetoothDevice device, int requestId, final BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, final byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
            Log.d(TAG, "onDescriptorWriteRequest : " + Arrays.toString(value));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);

            for (OnWriteRequestListener listener : mOnWriteRequestListeners) {
                listener.onDescriptorWritten(device, descriptor, value);
            }

        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
        }
    };

    /**
     * Open the advertising, let other phone can search for this device using Bluetooth.
     *
     * @param serviceUuid Parameters needed for other Bluetooth devices to filter and scan.
     * @return Success or failure
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public boolean startAdvertising(UUID serviceUuid) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            Log.e(TAG, "Failed to create advertiser");
            return false;
        }

        AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .build();

        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(new ParcelUuid(serviceUuid))
                .build();

        mBluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, mAdvertiseCallback);
        return true;
    }

    /**
     * Stop Bluetooth LE advertising.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public boolean stopAdvertising() {
        if (mBluetoothLeAdvertiser == null) {
            return false;
        }
        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
        for (OnAdvertiseListener listener : mOnAdvertiseListeners) {
            listener.onStopAdvertising();
        }
        return true;
    }

    public boolean startServer(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return false;
        }
        mBluetoothGattServer = bluetoothManager.openGattServer(context, mBluetoothGattServerCallback);
        return true;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public boolean closeServer() {
        if (mBluetoothGattServer == null) {
            return false;
        }
        stopAdvertising();
        mBluetoothGattServer.close();
        return true;
    }

    public void addService(ServiceSettings settings) {
        BluetoothGattService service = new BluetoothGattService(settings.serviceUuid, settings.serviceType);
        List<ServiceProfile> profiles = settings.serviceProfiles;
        for (ServiceProfile profile : profiles) {
            BluetoothGattCharacteristic characteristic =
                    new BluetoothGattCharacteristic(profile.getCharacteristicUuid(), profile.getCharacteristicProperties(), profile.getCharacteristicPermission());
            if (profile.getDescriptorUuid() != null) {
                BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(profile.getDescriptorUuid(), profile.getDescriptorPermission());
                descriptor.setValue(profile.getDescriptorValue());
                characteristic.addDescriptor(descriptor);
            }
            service.addCharacteristic(characteristic);
        }
        mBluetoothGattServer.addService(service);
    }

    public void removeService(BluetoothGattService service) {
        mBluetoothGattServer.removeService(service);
    }

    public List<BluetoothGattService> getServices() {
        return mBluetoothGattServer.getServices();
    }

    public BluetoothGattServer getBluetoothGattServer() {
        return mBluetoothGattServer;
    }

    public List<BluetoothDevice> getConnectDevices() {
        return mConnectDevices;
    }

    public void sendReadCharacteristic(UUID serviceUuid, UUID characteristicUuid, byte[] value) {
        BluetoothGattCharacteristic characteristic = mBluetoothGattServer
                .getService(serviceUuid)
                .getCharacteristic(characteristicUuid);
        characteristic.setValue(value);
    }

    public void sendNotificationValue(BluetoothDevice device, UUID serviceUuid, UUID characteristicUuid, byte[] value) {
        BluetoothGattCharacteristic characteristic = mBluetoothGattServer
                .getService(serviceUuid)
                .getCharacteristic(characteristicUuid);
        characteristic.setValue(value);
        mBluetoothGattServer.notifyCharacteristicChanged(device, characteristic, false);
    }

    public void sendIndicationValue(BluetoothDevice device, UUID serviceUuid, UUID characteristicUuid, byte[] value) {
        BluetoothGattCharacteristic characteristic = mBluetoothGattServer
                .getService(serviceUuid)
                .getCharacteristic(characteristicUuid);
        characteristic.setValue(value);
        mBluetoothGattServer.notifyCharacteristicChanged(device, characteristic, true);
    }

    public void addOnAdvertiseListener(OnAdvertiseListener listener) {
        mOnAdvertiseListeners.add(listener);
    }

    public void removeAdvertiseStartListener(OnAdvertiseListener listener) {
        mOnAdvertiseListeners.remove(listener);
    }

    public void setOnConnectionStateChangeListener(OnConnectionStateChangeListener listener) {
        mOnConnectionStateChangeListeners.add(listener);
    }

    public void removeConnectionStateChangeListener(OnConnectionStateChangeListener listener) {
        mOnConnectionStateChangeListeners.remove(listener);
    }

    public void setOnServiceAddedListener(OnServiceAddedListener listener) {
        mOnServiceAddedListeners.add(listener);
    }

    public void removeServiceAddedListener(OnServiceAddedListener listener) {
        mOnServiceAddedListeners.remove(listener);
    }

    public void setOnWriteRequestListener(OnWriteRequestListener listener) {
        mOnWriteRequestListeners.add(listener);
    }

    public void removeWriteRequestListener(OnWriteRequestListener listener) {
        mOnWriteRequestListeners.remove(listener);
    }

    public void setOnReadRequestListener(OnReadRequestListener listener) {
        mOnReadRequestListeners.add(listener);
    }

    public void removeReadRequestListener(OnReadRequestListener listener) {
        mOnReadRequestListeners.remove(listener);
    }

    public void removeAllListeners() {
        mOnAdvertiseListeners.clear();
        mOnConnectionStateChangeListeners.clear();
        mOnServiceAddedListeners.clear();
        mOnWriteRequestListeners.clear();
        mOnReadRequestListeners.clear();
    }
}