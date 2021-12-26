/*
 * Copyright (c) 2016, Qin Dachang
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qindachang.bluetoothle;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private List<String> filterDeviceNameList = new ArrayList<>();
    private List<String> filterDeviceAddressList = new ArrayList<>();
    private List<UUID> uFilerServiceUUIDList = new ArrayList<>();
    private int scanPeriod;
    private int reportDelayMillis;

    public void init(Context context) {
        if (mBleManager == null) {
            mBleManager = new BleManager(context.getApplicationContext());
        }
    }

    public void init(Context context, BluetoothConfig config) {
        if (mBleManager == null) {
            mBleManager = new BleManager(context.getApplicationContext(), config);
        } else {
            mBleManager.setConfig(config);
        }
    }

    public void changeConfig(BluetoothConfig config) {
        mBleManager.setConfig(config);
    }

    public boolean isSupportBluetooth() {
        return mBleManager.isSupportBluetooth();
    }

    public boolean isBluetoothOpen() {
        return mBleManager.isBluetoothOpen();
    }

    public void enableBluetooth(Activity activity) {
        mBleManager.enableBluetooth(activity);
    }

    public void enableBluetooth(Activity activity, int requestCode) {
        mBleManager.enableBluetooth(activity, requestCode);
    }

    public void disableBluetooth() {
        mBleManager.disableBluetooth();
    }

    public boolean clearDeviceCache() {
        return mBleManager.clearDeviceCache();
    }

    public BluetoothLe setScanWithDeviceName(String deviceName) {
        this.filterDeviceNameList.add(deviceName);
        return this;
    }

    public BluetoothLe setScanWithDeviceName(String[] deviceNames) {
        Collections.addAll(this.filterDeviceNameList, deviceNames);
        return this;
    }

    public BluetoothLe setScanWithDeviceAddress(String deviceAddress) {
        this.filterDeviceAddressList.add(deviceAddress);
        return this;
    }

    public BluetoothLe setScanWithDeviceAddress(String[] deviceAddress) {
        Collections.addAll(this.filterDeviceAddressList, deviceAddress);
        return this;
    }

    public BluetoothLe setScanWithServiceUUID(String serviceUUID) {
        setScanWithServiceUUID(UUID.fromString(serviceUUID));
        return this;
    }

    public BluetoothLe setScanWithServiceUUID(String[] serviceUUIDs) {
        for (String serviceUUID : serviceUUIDs) {
            setScanWithServiceUUID(UUID.fromString(serviceUUID));
        }
        return this;
    }

    public BluetoothLe setScanWithServiceUUID(UUID serviceUUID) {
        this.uFilerServiceUUIDList.add(serviceUUID);
        return this;
    }

    public BluetoothLe setScanWithServiceUUID(UUID[] serviceUUIDs) {
        Collections.addAll(this.uFilerServiceUUIDList, serviceUUIDs);
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

    public void startScan() {
        mBleManager.scan(null, filterDeviceNameList, filterDeviceAddressList, uFilerServiceUUIDList, scanPeriod, reportDelayMillis);
        filterDeviceNameList.clear();
        filterDeviceAddressList.clear();
        uFilerServiceUUIDList.clear();
        scanPeriod = 0;
    }

    public void startScan(OnLeScanListener onLeScanListener) {
        startScan(null, onLeScanListener);
    }

    public void startScan(Activity activity, OnLeScanListener onLeScanListener) {
        mBleManager.setOnLeScanListener(onLeScanListener);
        mBleManager.scan(activity, filterDeviceNameList, filterDeviceAddressList, uFilerServiceUUIDList, scanPeriod, reportDelayMillis);
        filterDeviceNameList.clear();
        filterDeviceAddressList.clear();
        uFilerServiceUUIDList.clear();
        scanPeriod = 0;
    }

    public void startScan(Activity activity) {
        mBleManager.scan(activity, filterDeviceNameList, filterDeviceAddressList, uFilerServiceUUIDList, scanPeriod, reportDelayMillis);
        filterDeviceNameList.clear();
        filterDeviceAddressList.clear();
        uFilerServiceUUIDList.clear();
        scanPeriod = 0;
    }

    public void setOnScanListener(OnLeScanListener onLeScanListener) {
        mBleManager.setOnLeScanListener(onLeScanListener);
    }

    public void setOnScanListener(@NonNull Object tag, OnLeScanListener onLeScanListener) {
        onLeScanListener.setTag(tag);
        mBleManager.addLeListenerList(onLeScanListener);
    }

    public void stopScan() {
        mBleManager.stopScan();
    }

    public boolean getScanning() {
        return mBleManager.scanning();
    }

    public boolean getConnected() {
        return mBleManager.getConnected();
    }

    public BluetoothDevice getConnectedBluetoothDevice() {
        return mBleManager.getBluetoothDevice();
    }

    public BluetoothGatt getBluetoothGatt() {
        return mBleManager.getBluetoothGatt();
    }

    public boolean getServicesDiscovered() {
        return mBleManager.getServicesDiscovered();
    }

    public BluetoothLe setStopScanAfterConnected(boolean enable) {
        mBleManager.setStopScanAfterConnected(enable);
        return this;
    }

    public void startConnect(BluetoothDevice bluetoothDevice) {
        mBleManager.connect(false, bluetoothDevice);
    }

    public void startConnect(boolean autoConnect, BluetoothDevice bluetoothDevice) {
        mBleManager.connect(autoConnect, bluetoothDevice);
    }

    public void startConnect(BluetoothDevice bluetoothDevice, OnLeConnectListener onLeConnectListener) {
        startConnect(false, bluetoothDevice, onLeConnectListener);
    }

    public void startConnect(boolean autoConnect, BluetoothDevice bluetoothDevice, OnLeConnectListener onLeConnectListener) {
        setOnConnectListener(onLeConnectListener);
        mBleManager.connect(autoConnect, bluetoothDevice);
    }

    public void setOnConnectListener(OnLeConnectListener onLeConnectListener) {
        mBleManager.setConnectListener(onLeConnectListener);
    }

    public void setOnConnectListener(@NonNull Object tag, OnLeConnectListener onLeConnectListener) {
        onLeConnectListener.setTag(tag);
        mBleManager.addLeListenerList(onLeConnectListener);
    }

    public void disconnect() {
        mBleManager.disconnect();
    }

    public BluetoothLe enableNotification(boolean enable, String serviceUUID, String characteristicUUID) {
        enableNotification(enable, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
        return this;
    }

    public BluetoothLe enableNotification(boolean enable, UUID serviceUUID, UUID characteristicUUID) {
        enableNotification(enable, serviceUUID, new UUID[]{characteristicUUID});
        return this;
    }

    public BluetoothLe enableNotification(boolean enable, String serviceUUID, String[] characteristicUUIDs) {
        int length = characteristicUUIDs.length;
        UUID[] uuids = new UUID[length];
        for (int i = 0; i < length; i++) {
            uuids[i] = UUID.fromString(characteristicUUIDs[i]);
        }
        enableNotification(enable, UUID.fromString(serviceUUID), uuids);
        return this;
    }

    public BluetoothLe enableNotification(boolean enable, UUID serviceUUID, UUID[] characteristicUUIDs) {
        mBleManager.enableNotificationQueue(enable, serviceUUID, characteristicUUIDs);
        return this;
    }

    public void setOnNotificationListener(OnLeNotificationListener onLeNotificationListener) {
        mBleManager.setOnLeNotificationListener(onLeNotificationListener);
    }

    public void setOnNotificationListener(@NonNull Object tag, OnLeNotificationListener onLeNotificationListener) {
        onLeNotificationListener.setTag(tag);
        mBleManager.addLeListenerList(onLeNotificationListener);
    }

    public BluetoothLe enableIndication(boolean enable, String serviceUUID, String characteristicUUID) {
        enableIndication(enable, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
        return this;
    }

    public BluetoothLe enableIndication(boolean enable, UUID serviceUUID, UUID characteristicUUID) {
        enableIndication(enable, serviceUUID, new UUID[]{characteristicUUID});
        return this;
    }

    public BluetoothLe enableIndication(boolean enable, String serviceUUID, String[] characteristicUUIDs) {
        int length = characteristicUUIDs.length;
        UUID[] uuids = new UUID[length];
        for (int i = 0; i < length; i++) {
            uuids[i] = UUID.fromString(characteristicUUIDs[i]);
        }
        enableIndication(enable, UUID.fromString(serviceUUID), uuids);
        return this;
    }

    public BluetoothLe enableIndication(boolean enable, UUID serviceUUID, UUID[] characteristicUUIDs) {
        mBleManager.enableIndicationQueue(enable, serviceUUID, characteristicUUIDs);
        return this;
    }

    public void setOnIndicationListener(@NonNull Object tag, OnLeIndicationListener onLeIndicationListener) {
        onLeIndicationListener.setTag(tag);
        mBleManager.addLeListenerList(onLeIndicationListener);
    }

    public void readCharacteristic(String serviceUUID, String characteristicUUID) {
        mBleManager.readCharacteristicQueue(UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
    }

    public void readCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
        mBleManager.readCharacteristicQueue(serviceUUID, characteristicUUID);
    }

    public void readCharacteristic(String serviceUUID, String characteristicUUID, OnLeReadCharacteristicListener onLeReadCharacteristicListener) {
        readCharacteristic(UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), onLeReadCharacteristicListener);
    }

    public void readCharacteristic(UUID serviceUUID, UUID characteristicUUID, OnLeReadCharacteristicListener onLeReadCharacteristicListener) {
        mBleManager.readCharacteristicQueue(serviceUUID, characteristicUUID);
        setOnReadCharacteristicListener(onLeReadCharacteristicListener);
    }

    public void setOnReadCharacteristicListener(OnLeReadCharacteristicListener onReadCharacteristicListener) {
        mBleManager.setOnLeReadCharacteristicListener(onReadCharacteristicListener);
    }

    public void setOnReadCharacteristicListener(@NonNull Object tag, OnLeReadCharacteristicListener onReadCharacteristicListener) {
        onReadCharacteristicListener.setTag(tag);
        mBleManager.addLeListenerList(onReadCharacteristicListener);
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

    public void setOnWriteCharacteristicListener(@NonNull Object tag, OnLeWriteCharacteristicListener onLeWriteCharacteristicListener) {
        onLeWriteCharacteristicListener.setTag(tag);
        mBleManager.addLeListenerList(onLeWriteCharacteristicListener);
    }

    public BluetoothLe setReadRssiInterval(int millisecond) {
        mBleManager.setReadRssiIntervalMillisecond(millisecond);
        return this;
    }

    public void setOnReadRssiListener(@NonNull Object tag, OnLeReadRssiListener onLeReadRssiListener) {
        onLeReadRssiListener.setTag(tag);
        mBleManager.addLeListenerList(onLeReadRssiListener);
        mBleManager.readRssi();
    }

    public ConnParameters readConnectionParameters() {
        return mBleManager.getConnParameters();
    }

    public void stopReadRssi() {
        mBleManager.cancelReadRssiTimerTask();
    }

    public void close() {
        mBleManager.close();
    }

    public void destroy() {
        mBleManager.destroy();
    }

    public void destroy(@NonNull Object tag) {
        mBleManager.destroy(tag);
    }

    public void cancelTag(@NonNull Object tag) {
        mBleManager.cancelTag(tag);
    }

    public void cancelAllTag() {
        mBleManager.cancelAllTag();
    }

    public void clearQueue() {
        mBleManager.clearQueue();
    }
}
