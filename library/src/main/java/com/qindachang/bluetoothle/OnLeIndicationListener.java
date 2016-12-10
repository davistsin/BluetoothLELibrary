package com.qindachang.bluetoothle;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created on 2016/12/10.
 *
 * @author Qin DaChang
 * @see <a href="https://github.com/qindachang">https://github.com/qindachang</a>
 */

public abstract class OnLeIndicationListener extends LeListener{
    public abstract void onSuccess(BluetoothGattCharacteristic characteristic);
}
