package com.davistsin.bluetoothle.replicas;

import android.bluetooth.BluetoothGattService;

/**
 * Created by David Qin on 2017/4/21.
 */

public interface OnServiceAddedListener extends IServerListener {
    void onSuccess(BluetoothGattService service);

    void onFail(BluetoothGattService service);
}
