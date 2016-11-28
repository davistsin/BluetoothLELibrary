package com.qindachang.bluetoothle;

import android.bluetooth.BluetoothGattCharacteristic;


public interface OnLeNotificationListener extends LeListener{
    void onSuccess(BluetoothGattCharacteristic characteristic);

}
