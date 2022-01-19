package com.davistsin.bluetoothle.main.listener;

import android.bluetooth.BluetoothGattCharacteristic;

public interface BleReadCharacteristicListener {
    void onSuccess(BluetoothGattCharacteristic characteristic);

    void onFailure();
}
