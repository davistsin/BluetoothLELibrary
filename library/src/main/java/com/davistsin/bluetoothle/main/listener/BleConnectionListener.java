package com.davistsin.bluetoothle.main.listener;

import android.bluetooth.BluetoothDevice;

public interface BleConnectionListener {
    void onConnecting();

    void onConnected(BluetoothDevice device);

    void onDisconnected();

    void onFailure();
}
