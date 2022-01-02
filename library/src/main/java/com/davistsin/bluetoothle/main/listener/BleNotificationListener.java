package com.davistsin.bluetoothle.main.listener;

import android.bluetooth.BluetoothGattCharacteristic;

public interface BleNotificationListener {
    void onSuccess(BluetoothGattCharacteristic characteristic);
    void onFailed();
}
