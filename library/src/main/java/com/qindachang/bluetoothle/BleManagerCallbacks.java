package com.qindachang.bluetoothle;


interface BleManagerCallbacks {

    void onDeviceConnecting();

    void onDeviceConnected();

    void onDeviceDisconnecting();

    void onDeviceDisconnected();

    void onServicesDiscovered();

}
