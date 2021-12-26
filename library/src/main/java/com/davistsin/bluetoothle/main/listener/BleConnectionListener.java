package com.davistsin.bluetoothle.main.listener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import com.qindachang.bluetoothle.exception.ConnBleException;

public interface BleConnectionListener {
    void onConnecting();

    void onConnected();

    void onDisconnected();

    void onConnectFailure();
}
