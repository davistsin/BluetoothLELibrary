package com.davistsin.bluetoothle.main.connect;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

public class BleConnectCreator {

    private BleConnectCreator() {}

    public static BleConnector create(Context context, BluetoothDevice device) {
        return BleConnectCreator.create(context, device, new ConnectorSettings.Builder().build());
    }
    public static BleConnector create(Context context, BluetoothDevice device, ConnectorSettings settings) {
        return new BleConnector(context, device, settings);
    }
}
