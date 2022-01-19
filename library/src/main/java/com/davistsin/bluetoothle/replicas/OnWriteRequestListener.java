package com.davistsin.bluetoothle.replicas;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by David Qin on 2017/4/20.
 */

public interface OnWriteRequestListener extends IServerListener {
    void onCharacteristicWritten(BluetoothDevice device, BluetoothGattCharacteristic characteristic, byte[] value);

    void onDescriptorWritten(BluetoothDevice device, BluetoothGattDescriptor descriptor, byte[] value);
}
