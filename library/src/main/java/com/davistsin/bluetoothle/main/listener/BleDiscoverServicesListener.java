package com.davistsin.bluetoothle.main.listener;

import android.bluetooth.BluetoothGatt;

public interface BleDiscoverServicesListener {

    void onServicesDiscovered(BluetoothGatt gatt);

    void onFailure();
}
