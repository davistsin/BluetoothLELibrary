package com.qindachang.bluetoothle;

import android.bluetooth.BluetoothGatt;


public abstract class OnLeConnectListener extends LeListener {

    public abstract void onDeviceConnecting();

    public abstract void onDeviceConnected();

    public abstract void onDeviceDisconnected();

    public abstract void onServicesDiscovered(BluetoothGatt gatt);

    public abstract void onDeviceConnectFail();

}
