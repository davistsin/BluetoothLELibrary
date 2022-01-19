package com.davistsin.bluetoothle.replicas;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by David Qin on 2017/4/20.
 */

public interface OnReadRequestListener extends IServerListener {
    void onCharacteristicRead(BluetoothDevice device, BluetoothGattCharacteristic characteristic);

    void onDescriptorRead(BluetoothDevice device, BluetoothGattDescriptor descriptor);
}
