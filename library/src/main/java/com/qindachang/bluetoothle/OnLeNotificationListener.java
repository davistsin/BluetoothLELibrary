package com.qindachang.bluetoothle;

import android.bluetooth.BluetoothGattCharacteristic;


public interface OnLeNotificationListener {
    void onSuccess(BluetoothGattCharacteristic characteristic);

    void onFailure();
}
