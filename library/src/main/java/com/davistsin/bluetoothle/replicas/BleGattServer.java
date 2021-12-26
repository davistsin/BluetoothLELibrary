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
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by David Qin on 2017/4/19.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class BleGattServer {
    private static final String TAG = BleGattServer.class.getSimpleName();

    public static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothGattServer mBluetoothGattServer;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private List<BluetoothDevice> mConnectDevices = new ArrayList<>();

    private OnAdvertiseListener mOnAdvertiseListener;
    private OnConnectionStateChangeListener mOnConnectionStateChangeListener;
    private OnServiceAddedListener mOnServiceAddedListener;
    private OnWriteRequestListener mOnWriteRequestListener;
    private OnReadRequestListener mOnReadRequestListener;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private void runOnUiThread(Runnable runnable) {
        if (isAndroidMainThread()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }

    private static boolean isAndroidMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(final AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);

            if (mOnAdvertiseListener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnAdvertiseListener.onStartSuccess(settingsInEffect);
                    }
                });
            }

        }

        @Override
        public void onStartFailure(final int errorCode) {
            super.onStartFailure(errorCode);

            if (mOnAdvertiseListener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnAdvertiseListener.onStartFailure(errorCode);
                    }
                });
            }
        }
    };

    private BluetoothGattServerCallback mBluetoothGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothDevice device, final int status, final int newState) {
            super.onConnectionStateChange(device, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectDevices.add(device);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectDevices.remove(device);
            }

            if (mOnConnectionStateChangeListener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            mOnConnectionStateChangeListener.onConnected(device);
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            mOnConnectionStateChangeListener.onDisconnected(device);
                        }
                        mOnConnectionStateChangeListener.onChange(device, status, newState);
                    }
                });
            }

        }

        @Override
        public void onServiceAdded(final int status, final BluetoothGattService service) {
            super.onServiceAdded(status, service);

            if (mOnServiceAddedListener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            mOnServiceAddedListener.onSuccess(service);
                        } else {
                            mOnServiceAddedListener.onFail(service);
                        }
                    }
                });
            }

        }

        @Override
        public void onCharacteristicReadRequest(final BluetoothDevice device, int requestId, int offset, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.d(TAG, "onCharacteristicReadRequest : " + Arrays.toString(characteristic.getValue()));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, characteristic.getValue());

            if (mOnReadRequestListener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnReadRequestListener.onCharacteristicRead(device, characteristic);
                    }
                });
            }
        }

        @Override
        public void onCharacteristicWriteRequest(final BluetoothDevice device, int requestId, final BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, final byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            Log.d(TAG, "onCharacteristicWriteRequest : " + Arrays.toString(value));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);

            if (mOnWriteRequestListener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnWriteRequestListener.onCharacteristicWritten(device, characteristic, value);
                    }
                });
            }
        }

        @Override
        public void onDescriptorReadRequest(final BluetoothDevice device, int requestId, int offset, final BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            Log.d(TAG, "onDescriptorReadRequest : " + Arrays.toString(descriptor.getValue()));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, descriptor.getValue());

            if (mOnReadRequestListener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnReadRequestListener.onDescriptorRead(device, descriptor);
                    }
                });
            }
        }

        @Override
        public void onDescriptorWriteRequest(final BluetoothDevice device, int requestId, final BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, final byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
            Log.d(TAG, "onDescriptorWriteRequest : " + Arrays.toString(value));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);

            if (mOnWriteRequestListener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnWriteRequestListener.onDescriptorWritten(device, descriptor, value);
                    }
                });
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
        if (mOnAdvertiseListener != null) {
            mOnAdvertiseListener.onStopAdvertising();
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

    public void setOnAdvertiseListener(OnAdvertiseListener onAdvertiseListener) {
        mOnAdvertiseListener = onAdvertiseListener;
    }

    public void removeAdvertiseStartListener() {
        mOnAdvertiseListener = null;
    }

    public void setOnConnectionStateChangeListener(OnConnectionStateChangeListener onConnectionStateChangeListener) {
        mOnConnectionStateChangeListener = onConnectionStateChangeListener;
    }

    public void removeConnectionStateChangeListener() {
        mOnConnectionStateChangeListener = null;
    }

    public void setOnServiceAddedListener(OnServiceAddedListener onServiceAddedListener) {
        mOnServiceAddedListener = onServiceAddedListener;
    }

    public void removeServiceAddedListener() {
        mOnServiceAddedListener = null;
    }

    public void setOnWriteRequestListener(OnWriteRequestListener onWriteRequestListener) {
        mOnWriteRequestListener = onWriteRequestListener;
    }

    public void removeWriteRequestListener() {
        mOnWriteRequestListener = null;
    }

    public void setOnReadRequestListener(OnReadRequestListener onReadRequestListener) {
        mOnReadRequestListener = onReadRequestListener;
    }

    public void removeReadRequestListener() {
        mOnReadRequestListener = null;
    }
}