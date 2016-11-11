package com.qindachang.bluetoothle;

import android.bluetooth.BluetoothGattCharacteristic;

class Request {


    enum Type {
        WRITE,
        READ,
        ENABLE_NOTIFICATIONS,
        ENABLE_INDICATIONS
    }

    public final Type type;
    private final BluetoothGattCharacteristic characteristic;
    private boolean enable;

    private Request(final Type type, final BluetoothGattCharacteristic characteristic) {
        this.type = type;
        this.characteristic = characteristic;
    }

    private Request(final Type type, final BluetoothGattCharacteristic characteristic, boolean enable) {
        this.type = type;
        this.characteristic = characteristic;
        this.enable = enable;
    }

    public static Request newReadRequest(final BluetoothGattCharacteristic characteristic) {
        return new Request(Type.READ, characteristic);
    }

    public static Request newWriteRequest(final BluetoothGattCharacteristic characteristic) {
        return new Request(Type.WRITE, characteristic);
    }

    public static Request newEnableNotificationsRequest(final boolean enable, final BluetoothGattCharacteristic characteristic) {
        return new Request(Type.ENABLE_NOTIFICATIONS, characteristic, enable);
    }

    public static Request newEnableIndicationsRequest(final BluetoothGattCharacteristic characteristic) {
        return new Request(Type.ENABLE_INDICATIONS, characteristic);
    }


    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }


    public boolean isEnable() {
        return enable;
    }
}
