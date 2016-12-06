package com.qindachang.bluetoothle;


import android.bluetooth.BluetoothGattCharacteristic;

public abstract class OnLeReadCharacteristicListener extends LeListener{

    public abstract void onSuccess(BluetoothGattCharacteristic characteristic);

    public abstract void onFailure(String info, int status);
}
