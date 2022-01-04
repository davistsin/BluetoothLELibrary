package com.davistsin.bluetoothle.main.connect;

import static com.davistsin.bluetoothle.main.connect.BluetoothUtil.bytes2IntegerList;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.davistsin.bluetoothle.main.listener.BleConnectionListener;
import com.davistsin.bluetoothle.main.listener.BleDiscoverServicesListener;
import com.davistsin.bluetoothle.main.listener.BleIndicationListener;
import com.davistsin.bluetoothle.main.listener.BleNotificationListener;
import com.davistsin.bluetoothle.main.listener.BleReadCharacteristicListener;
import com.davistsin.bluetoothle.main.listener.BleRssiListener;
import com.davistsin.bluetoothle.main.listener.BleWriteCharacteristicListener;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

public class BleConnector {
    private static final String TAG = BleConnector.class.getSimpleName();

    private static final UUID SERVICE = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final UUID PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS_UUID = UUID.fromString("00002A04-0000-1000-8000-00805f9b34fb");

    private final Set<BleConnectionListener> mConnectionListeners = new CopyOnWriteArraySet<>();
    private final Set<BleDiscoverServicesListener> mDiscoverServicesListeners = new CopyOnWriteArraySet<>();
    private final Set<BleWriteCharacteristicListener> mWriteCharacteristicListeners = new CopyOnWriteArraySet<>();
    private final Set<BleReadCharacteristicListener> mReadCharacteristicListeners = new CopyOnWriteArraySet<>();
    private final Set<BleNotificationListener> mNotificationListeners = new CopyOnWriteArraySet<>();
    private final Set<BleIndicationListener> mIndicationListeners = new CopyOnWriteArraySet<>();
    private final Set<BleRssiListener> mRssiListeners = new CopyOnWriteArraySet<>();

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final RequestQueue mRequestQueue = new RequestQueue();

    private final WeakReference<Context> mWeakContext;
    private final BluetoothDevice mBluetoothDevice;

    private BluetoothGatt mBluetoothGatt;
    private ConnectorSettings mConnectorSettings;
    private ConnParameters mConnParameters;

    private volatile boolean connected;

    BleConnector(Context context, BluetoothDevice device, ConnectorSettings settings) {
        mWeakContext = new WeakReference<>(context);
        mBluetoothDevice = device;
        mConnectorSettings = settings;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTING:
                    for (BleConnectionListener listener : mConnectionListeners) {
                        listener.onConnecting();
                    }
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    connected = true;

                    if (mConnectorSettings.autoDiscoverServices) {
                        discoverServices();
                    }

                    for (BleConnectionListener listener : mConnectionListeners) {
                        listener.onConnected(mBluetoothDevice);
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    connected = false;

                    for (BleConnectionListener listener : mConnectionListeners) {
                        listener.onDisconnected();
                    }
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (BleDiscoverServicesListener listener : mDiscoverServicesListeners) {
                    listener.onServicesDiscovered(gatt);
                }

                readConnectionParameters();
            } else {
                for (BleDiscoverServicesListener listener : mDiscoverServicesListeners) {
                    listener.onFailure();
                }
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            switch (status) {
                case BluetoothGatt.GATT_SUCCESS:
                    if (characteristic.getUuid().equals(PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS_UUID)) {
                        List<Integer> parameters = bytes2IntegerList(characteristic.getValue());
                        double connIntervalMin = (parameters.get(1) * 16 + parameters.get(0)) * 1.25;
                        double connIntervalMax = (parameters.get(3) * 16 + parameters.get(2)) * 1.25;
                        int slaveLatency = parameters.get(5) * 16 + parameters.get(4);
                        int connSupervisionTimeout = parameters.get(7) * 16 + parameters.get(6);
                        mConnParameters = new ConnParameters();
                        mConnParameters.setUUID(PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS_UUID);
                        mConnParameters.setConnIntervalMin(connIntervalMin);
                        mConnParameters.setConnIntervalMax(connIntervalMax);
                        mConnParameters.setProperties("READ");
                        mConnParameters.setSlaveLatency(slaveLatency);
                        mConnParameters.setSupervisionTimeout(connSupervisionTimeout);
                        if (mConnectorSettings.queueIntervalTime == ConnectorSettings.QUEUE_INTERVAL_TIME_AUTO) {
                            mConnectorSettings.queueIntervalTime = (int) connIntervalMax + 20;
                        }
                    } else {
                        for (BleReadCharacteristicListener listener: mReadCharacteristicListeners) {
                            listener.onSuccess(characteristic);
                        }
                    }
                    break;
                case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
                    for (BleReadCharacteristicListener listener: mReadCharacteristicListeners) {
                        listener.onFailure();
                    }
                    break;
                default:
                    for (BleReadCharacteristicListener listener: mReadCharacteristicListeners) {
                        listener.onFailure();
                    }
                    break;
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            switch (status) {
                case BluetoothGatt.GATT_SUCCESS:
                    for (BleWriteCharacteristicListener listener: mWriteCharacteristicListeners) {
                        listener.onSuccess(characteristic);
                    }
                    break;
                case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
                    for (BleWriteCharacteristicListener listener: mWriteCharacteristicListeners) {
                        listener.onFailed();
                    }
                    break;
                default:
                    for (BleWriteCharacteristicListener listener: mWriteCharacteristicListeners) {
                        listener.onFailed();
                    }
                    break;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            BluetoothGattDescriptor cccd = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
            boolean notifications = cccd == null || cccd.getValue() == null || cccd.getValue().length != 2 || cccd.getValue()[0] == 0x01;


            if (notifications) {
                for (BleNotificationListener listener: mNotificationListeners) {
                    listener.onSuccess(characteristic);
                }
            } else {
                for (BleIndicationListener listener : mIndicationListeners) {
                    listener.onSuccess(characteristic);
                }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (BleRssiListener listener : mRssiListeners) {
                    listener.onSuccess(rssi);
                }
            }
        }
    };

    public synchronized boolean connect() {
        if (mBluetoothGatt != null) {
           return mBluetoothGatt.connect();
        }
        mBluetoothGatt = mBluetoothDevice.connectGatt(mWeakContext.get(), mConnectorSettings.autoConnect, mGattCallback);
        return true;
    }

    public synchronized boolean disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            return true;
        }
        return false;
    }

    public synchronized boolean discoverServices() {
        return mBluetoothGatt.discoverServices();
    }

    public void writeCharacteristic(byte[] bytes, String serviceUUID, String characteristicUUID) {
        writeCharacteristic(bytes, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
    }

    public void writeCharacteristic(byte[] bytes, UUID serviceUUID, UUID characteristicUUID) {
        if (!connected) {
            Log.e(TAG, "Error: writeCharacteristic(). Bluetooth not connected.");
            return;
        }
        BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
        if (service == null) {
            Log.e(TAG, "Error: writeCharacteristic(). Can not find service by UUID: " + serviceUUID.toString());
            return;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
        if (characteristic == null) {
            Log.e(TAG, "Error: writeCharacteristic(). Can not find characteristic by UUID: " + serviceUUID.toString());
            return;
        }
        mRequestQueue.addRequest(Request.newWriteRequest(characteristic, bytes));
    }

    public void readCharacteristic(String serviceUUID, String characteristicUUID) {
        readCharacteristic(UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
    }

    public void readCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
        if (!connected) {
            Log.e(TAG, "Error: readCharacteristic(). Bluetooth not connected.");
            return;
        }
        BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
        if (service == null) {
            Log.e(TAG, "Error: readCharacteristic(). Can not find service by UUID: " + serviceUUID.toString());
            return;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
        if (characteristic == null) {
            Log.e(TAG, "Error: readCharacteristic(). Can not find characteristic by UUID: " + serviceUUID.toString());
            return;
        }
        mRequestQueue.addRequest(Request.newReadRequest(characteristic));
    }

    public void enableNotification(boolean enable, String serviceUUID, String characteristicUUID) {
        enableNotification(enable, serviceUUID, new String[]{characteristicUUID});
    }

    public void enableNotification(boolean enable, String serviceUUID, String[] characteristicUUIDs) {
        UUID[] uuids = new UUID[characteristicUUIDs.length];
        for (int i = 0; i < characteristicUUIDs.length; i++) {
            uuids[i] = UUID.fromString(characteristicUUIDs[i]);
        }
        enableNotification(enable, UUID.fromString(serviceUUID), uuids);
    }

    public void enableNotification(boolean enable, UUID serviceUUID, UUID[] characteristicUUIDs) {
        if (!connected) {
            Log.e(TAG, "Error: enableNotification(). Bluetooth not connected.");
            return;
        }
        BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
        if (service == null) {
            Log.e(TAG, "Error: enableNotification(). Can not find service by UUID: " + serviceUUID.toString());
            return;
        }
        for (UUID characteristicUUID : characteristicUUIDs) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            if (characteristic == null) {
                Log.e(TAG, "Error: enableNotification(). Can not find characteristic by UUID: " + serviceUUID.toString());
                continue;
            }
            mRequestQueue.addRequest(Request.newEnableNotificationsRequest(enable, characteristic));
        }
    }

    public void enableIndication(boolean enable, String serviceUUID, String characteristicUUID) {
        enableIndication(enable, serviceUUID, new String[]{characteristicUUID});
    }

    public void enableIndication(boolean enable, String serviceUUID, String[] characteristicUUIDs) {
        UUID[] uuids = new UUID[characteristicUUIDs.length];
        for (int i = 0; i < characteristicUUIDs.length; i++) {
            uuids[i] = UUID.fromString(characteristicUUIDs[i]);
        }
        enableIndication(enable, UUID.fromString(serviceUUID), uuids);
    }

    public void enableIndication(boolean enable, UUID serviceUUID, UUID[] characteristicUUIDs) {
        if (!connected) {
            Log.e(TAG, "Error: enableIndication(). Bluetooth not connected.");
            return;
        }
        BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
        if (service == null) {
            Log.e(TAG, "Error: enableIndication(). Can not find service by UUID: " + serviceUUID.toString());
            return;
        }
        for (UUID characteristicUUID : characteristicUUIDs) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            if (characteristic == null) {
                Log.e(TAG, "Error: enableIndication(). Can not find characteristic by UUID: " + serviceUUID.toString());
                continue;
            }
            mRequestQueue.addRequest(Request.newEnableIndicationsRequest(enable, characteristic));
        }
    }

    public BleConnector addConnectionListener(BleConnectionListener bleConnectionListener) {
        mConnectionListeners.add(bleConnectionListener);
        return this;
    }

    public BleConnector addDiscoveryListener(BleDiscoverServicesListener bleDiscoverServicesListener) {
        mDiscoverServicesListeners.add(bleDiscoverServicesListener);
        return this;
    }

    public BleConnector addWriteCharacteristicListener(BleWriteCharacteristicListener bleWriteCharacteristicListener) {
        mWriteCharacteristicListeners.add(bleWriteCharacteristicListener);
        return this;
    }

    public BleConnector addReadCharacteristicListener(BleReadCharacteristicListener bleReadCharacteristicListener) {
        mReadCharacteristicListeners.add(bleReadCharacteristicListener);
        return this;
    }

    public BleConnector addNotificationListener(BleNotificationListener bleNotificationListener) {
        mNotificationListeners.add(bleNotificationListener);
        return this;
    }

    public BleConnector addIndicationListener(BleIndicationListener bleIndicationListener) {
        mIndicationListeners.add(bleIndicationListener);
        return this;
    }

    public BleConnector addRssiListener(BleRssiListener bleRssiListener) {
        mRssiListeners.add(bleRssiListener);
        return this;
    }

    public BleConnector removeConnectionListeners(BleConnectionListener... listeners) {
        mConnectionListeners.removeAll(Arrays.asList(listeners));
        return this;
    }

    public BleConnector removeDiscoveryListeners(BleDiscoverServicesListener... listeners) {
        mDiscoverServicesListeners.removeAll(Arrays.asList(listeners));
        return this;
    }

    public BleConnector removeNotificationListeners(BleNotificationListener... listeners) {
        mNotificationListeners.removeAll(Arrays.asList(listeners));
        return this;
    }

    public BleConnector removeIndicationListeners(BleIndicationListener... listeners) {
        mIndicationListeners.removeAll(Arrays.asList(listeners));
        return this;
    }

    public BleConnector removeRssiListeners(BleRssiListener... listeners) {
        mRssiListeners.removeAll(Arrays.asList(listeners));
        return this;
    }

    public BleConnector removeAllListeners() {
        mConnectionListeners.clear();
        mDiscoverServicesListeners.clear();
        mNotificationListeners.clear();
        mIndicationListeners.clear();
        mReadCharacteristicListeners.clear();
        mWriteCharacteristicListeners.clear();
        mRssiListeners.clear();
        return this;
    }

    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void changeSettings(ConnectorSettings settings) {
        mConnectorSettings = settings;
    }

    public boolean getConnected() {
        return connected;
    }

    public ConnParameters getConnParameters() {
        return mConnParameters;
    }

    public void close() {
        removeAllListeners();
        mRequestQueue.cancel();
        disconnect();
        mBluetoothGatt = null;
    }

    private void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt == null || characteristic == null) {
            return;
        }
        int properties = characteristic.getProperties();
        if ((properties & (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) == 0) {
            Log.e(TAG, "Error: writeCharacteristic(). " + characteristic.getUuid().toString() + " can not be written");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt == null || characteristic == null) {
            return;
        }
        int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) == 0) {
            Log.e(TAG, "Error: readCharacteristic(). " + characteristic.getUuid().toString() + " is not readable");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    private void enableNotification(boolean enable, BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt == null || characteristic == null) {
            return;
        }
        int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
            Log.e(TAG, "Error: enableNotification(). " + characteristic.getUuid().toString() + " not supports notification");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enable);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor == null) {
            return;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    private void enableIndication(boolean enable, BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt == null || characteristic == null) {
            return;
        }
        int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) == 0) {
            Log.e(TAG, "Error: enableIndication(). " + characteristic.getUuid().toString() + " not supports indication");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enable);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor == null) {
            return;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * 读取已连接设备的一些硬件信息
     */
    private void readConnectionParameters() {
        if (mBluetoothGatt != null) {
            BluetoothGattService service = mBluetoothGatt.getService(SERVICE);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS_UUID);
                if (characteristic != null) {
                    mRequestQueue.addRequest(Request.newReadRequest(characteristic));
                }
            }
        }
    }

    private class RequestQueue {
        private final Queue<Request> mRequestQueue = new LinkedBlockingQueue<>();

        synchronized void addRequest(Request request) {
            int oldSize = mRequestQueue.size();
            mRequestQueue.add(request);
            if (mRequestQueue.size() == 1 && oldSize == 0) {
                startExecutor();
            }
        }

        private void startExecutor() {
            Request request = mRequestQueue.peek();
            if (request == null) {
                return;
            }
            switch (request.type) {
                case WRITE:
                    BluetoothGattCharacteristic characteristic = request.getCharacteristic();
                    characteristic.setValue(request.getBytes());
                    writeCharacteristic(characteristic);
                    break;
                case READ:
                    readCharacteristic(request.getCharacteristic());
                    break;
                case ENABLE_NOTIFICATIONS:
                    enableNotification(request.isEnable(), request.getCharacteristic());
                    break;
                case ENABLE_INDICATIONS:
                    enableIndication(request.isEnable(), request.getCharacteristic());
                    break;
            }
            next();
        }

        void next() {
            if (mConnectorSettings.enableQueue && mConnectorSettings.queueIntervalTime > 0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runQueue();
                    }
                }, mConnectorSettings.queueIntervalTime);
            } else {
                runQueue();
            }
        }

        void runQueue() {
            mRequestQueue.poll();
            if (mRequestQueue.size() > 0) {
                startExecutor();
            }
        }

        void cancel() {
            mRequestQueue.clear();
        }
    }
}
