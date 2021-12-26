package com.davistsin.bluetoothle.replicas;

import android.bluetooth.BluetoothDevice;

/**
 * Created by David Qin on 2017/4/20.
 */

public interface OnConnectionStateChangeListener extends IServerListener {
    void onChange(BluetoothDevice device, int status, int newState);

    void onConnected(BluetoothDevice device);

    void onDisconnected(BluetoothDevice device);
}
