package com.qindachang.bluetoothle;

import android.bluetooth.BluetoothGatt;


public interface OnLeConnectListener {

    void onDeviceConnecting();

    void onDeviceConnected();

    void onDeviceDisconnected();

    void onServicesDiscovered(BluetoothGatt gatt);

    void onDeviceConnectFail();

}
