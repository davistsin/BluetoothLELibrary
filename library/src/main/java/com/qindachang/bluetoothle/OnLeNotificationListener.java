package com.qindachang.bluetoothle;

import android.bluetooth.BluetoothGattCharacteristic;


public abstract class OnLeNotificationListener extends LeListener{
    public abstract void onSuccess(BluetoothGattCharacteristic characteristic);

}
