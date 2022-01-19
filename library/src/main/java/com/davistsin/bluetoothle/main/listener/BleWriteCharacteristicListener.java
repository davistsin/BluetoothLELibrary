package com.davistsin.bluetoothle.main.listener;

import android.bluetooth.BluetoothGattCharacteristic;

public interface BleWriteCharacteristicListener {
    void onSuccess(BluetoothGattCharacteristic characteristic);

    void onFailed();
}
