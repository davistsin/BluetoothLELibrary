package com.qindachang.bluetoothle;

import android.bluetooth.BluetoothGattCharacteristic;


public interface OnLeWriteCharacteristicListener {
    void onSuccess(BluetoothGattCharacteristic characteristic);

    void onFailed(String msg, int status);
}
