package com.qindachang.bluetoothle;

import android.bluetooth.BluetoothGattCharacteristic;


public abstract class OnLeWriteCharacteristicListener extends LeListener{
    public abstract void onSuccess(BluetoothGattCharacteristic characteristic);

    public abstract void onFailed(String msg, int status);
}
