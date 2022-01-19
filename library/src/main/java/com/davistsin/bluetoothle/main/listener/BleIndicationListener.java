package com.davistsin.bluetoothle.main.listener;

import android.bluetooth.BluetoothGattCharacteristic;

import com.qindachang.bluetoothle.exception.BleException;

public interface BleIndicationListener {
    void onSuccess(BluetoothGattCharacteristic characteristic);
    void onFailed();
}
