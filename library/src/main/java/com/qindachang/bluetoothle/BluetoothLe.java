package com.qindachang.bluetoothle;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.UUID;

public class BluetoothLe {

    private static class SingletonHolder {
        private static final BluetoothLe INSTANCE = new BluetoothLe();
    }

    private BluetoothLe() {
    }

    public static BluetoothLe getDefault() {
        return SingletonHolder.INSTANCE;
    }

    private BleManager mBleManager;

    private String filterDeviceName;
    private String filterDeviceAddress;
    private UUID uFilerServiceUUID;
    private int scanPeriod;
    private int reportDelayMillis;

    public void init(Context context) {
        if (mBleManager == null) {
            mBleManager = new BleManager(context.getApplicationContext());
        }
    }

    public boolean isBluetoothOpen() {
        return mBleManager.isBluetoothOpen();
    }

    public void enableBluetooth(Activity activity,boolean enable) {
        mBleManager.enableBluetooth(activity,enable);
    }

    public boolean clearDeviceCache() {
       return mBleManager.clearDeviceCache();
    }

    public BluetoothLe setScanWithDeviceName(String deviceName) {
        this.filterDeviceName = deviceName;
        return this;
    }

    public BluetoothLe setScanWithDeviceAddress(String deviceAddress) {
        this.filterDeviceAddress = deviceAddress;
        return this;
    }

    public BluetoothLe setScanWithServiceUUID(String serviceUUID) {
        setScanWithServiceUUID(UUID.fromString(serviceUUID));
        return this;
    }

    public BluetoothLe setScanWithServiceUUID(UUID serviceUUID) {
        this.uFilerServiceUUID = serviceUUID;
        return this;
    }

    public BluetoothLe setScanPeriod(int millisecond) {
        this.scanPeriod = millisecond;
        return this;
    }

    public BluetoothLe setReportDelay(int reportDelayMillis) {
        this.reportDelayMillis = reportDelayMillis;
        return this;
    }

    public void startBleScan(Activity activity, OnLeScanListener onLeScanListener) {
        mBleManager.scan(activity, filterDeviceName, filterDeviceAddress, uFilerServiceUUID, scanPeriod, reportDelayMillis, onLeScanListener);
        filterDeviceName = null;
        filterDeviceAddress = null;
        uFilerServiceUUID = null;
        scanPeriod = 0;
    }

    public void stopBleScan() {
        mBleManager.stopScan();
    }

    public boolean getScanning() {
        return mBleManager.scanning();
    }

    public boolean getConnected() {
        return mBleManager.getConnected();
    }

    public BluetoothLe setStopScanAfterConnected(boolean enable) {
        mBleManager.setStopScanAfterConnected(enable);
        return this;
    }

    public void startBleConnect(BluetoothDevice bluetoothDevice) {
        startBleConnect(bluetoothDevice, null);
    }

    public void startBleConnect(BluetoothDevice bluetoothDevice, OnLeConnectListener onLeConnectListener) {
        startBleConnect(false, bluetoothDevice, onLeConnectListener);
    }

    public void startBleConnect(boolean autoConnect, BluetoothDevice bluetoothDevice, OnLeConnectListener onLeConnectListener) {
        mBleManager.connect(autoConnect, bluetoothDevice, onLeConnectListener);
    }

    public void setBleConnectListener(OnLeConnectListener onLeConnectListener) {
        mBleManager.setConnectListener(onLeConnectListener);
    }

    public void disconnect() {
        mBleManager.disconnect();
    }

    public BluetoothLe enableBleNotification(boolean enable, String serviceUUID, String characteristicUUID) {
        enableBleNotification(enable, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
        return this;
    }

    public BluetoothLe enableBleNotification(boolean enable, UUID serviceUUID, UUID characteristicUUID) {
        enableBleNotification(enable, serviceUUID, new UUID[]{characteristicUUID});
        return this;
    }

    public BluetoothLe enableBleNotification(boolean enable, String serviceUUID, String[] characteristicUUIDs) {
        int length = characteristicUUIDs.length;
        UUID[] uuids = new UUID[length];
        for (int i = 0; i < length; i++) {
            uuids[i] = UUID.fromString(characteristicUUIDs[i]);
        }
        enableBleNotification(enable, UUID.fromString(serviceUUID), uuids);
        return this;
    }

    public BluetoothLe enableBleNotification(boolean enable, UUID serviceUUID, UUID[] characteristicUUIDs) {
        mBleManager.enableNotificationQueue(enable, serviceUUID, characteristicUUIDs);
        return this;
    }

    public void setBleNotificationListener(OnLeNotificationListener onLeNotificationListener) {
        mBleManager.setOnLeNotificationListener(onLeNotificationListener);
    }

    public void readCharacteristic(String serviceUUID, String characteristicUUID) {
        mBleManager.readCharacteristicQueue(UUID.fromString(serviceUUID),UUID.fromString(characteristicUUID));
    }

    public void readCharacteristic(UUID serviceUUID,UUID characteristicUUID) {
        mBleManager.readCharacteristicQueue(serviceUUID, characteristicUUID);
    }

    public void readCharacteristic(String serviceUUID, String characteristicUUID, OnLeReadCharacteristicListener onLeReadCharacteristicListener) {
        readCharacteristic(UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), onLeReadCharacteristicListener);
    }

    public void readCharacteristic(UUID serviceUUID, UUID characteristicUUID, OnLeReadCharacteristicListener onLeReadCharacteristicListener) {
        mBleManager.readCharacteristicQueue(serviceUUID,characteristicUUID);
        setOnReadCharacteristicListener(onLeReadCharacteristicListener);
    }

    public void setOnReadCharacteristicListener(OnLeReadCharacteristicListener onReadCharacteristicListener) {
        mBleManager.setOnLeReadCharacteristicListener(onReadCharacteristicListener);
    }

    public void writeDataToCharacteristic(byte[] bytes, String serviceUUID, String characteristicUUID) {
        writeDataToCharacteristic(bytes, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
    }

    public void writeDataToCharacteristic(byte[] bytes, UUID serviceUUID, UUID characteristicUUID) {
        mBleManager.writeCharacteristicQueue(bytes, serviceUUID, characteristicUUID);
    }

    public void writeDataToCharacteristic(byte[] bytes, String serviceUUID, String characteristicUUID, OnLeWriteCharacteristicListener onLeWriteCharacteristicListener) {
        writeDataToCharacteristic(bytes, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), onLeWriteCharacteristicListener);
    }

    public void writeDataToCharacteristic(byte[] bytes, UUID serviceUUID, UUID characteristicUUID, OnLeWriteCharacteristicListener onLeWriteCharacteristicListener) {
        setOnWriteCharacteristicListener(onLeWriteCharacteristicListener);
        mBleManager.writeCharacteristicQueue(bytes, serviceUUID, characteristicUUID);
    }

    public void setOnWriteCharacteristicListener(OnLeWriteCharacteristicListener onLeWriteCharacteristicListener) {
        mBleManager.setWriteCharacteristicListener(onLeWriteCharacteristicListener);
    }

    public void close() {
        mBleManager.close();
    }

    public void destroy() {
        mBleManager.destroy();
    }

    public void clearQueue() {
        mBleManager.clearQueue();
    }
}
