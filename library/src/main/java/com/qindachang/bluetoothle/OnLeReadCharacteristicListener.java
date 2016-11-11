package com.qindachang.bluetoothle;


import android.bluetooth.BluetoothGattCharacteristic;

public interface OnLeReadCharacteristicListener {

    void onSuccess(BluetoothGattCharacteristic characteristic);

    void onFailure(String info, int status);
}
