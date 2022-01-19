package com.davistsin.bluetoothle.main.connect;

import android.bluetooth.BluetoothGattCharacteristic;

/* package */ class Request {

    private byte[] mBytes;

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

    private Request(final Type type, final BluetoothGattCharacteristic characteristic, byte[] bytes) {
        this.type = type;
        this.characteristic = characteristic;
        this.mBytes = bytes;
    }

    private Request(final Type type, final BluetoothGattCharacteristic characteristic, boolean enable) {
        this.type = type;
        this.characteristic = characteristic;
        this.enable = enable;
    }

    public static Request newReadRequest(final BluetoothGattCharacteristic characteristic) {
        return new Request(Type.READ, characteristic);
    }

    public static Request newWriteRequest(final BluetoothGattCharacteristic characteristic, byte[] bytes) {
        return new Request(Type.WRITE, characteristic, bytes);
    }

    public static Request newEnableNotificationsRequest(final boolean enable, final BluetoothGattCharacteristic characteristic) {
        return new Request(Type.ENABLE_NOTIFICATIONS, characteristic, enable);
    }

    public static Request newEnableIndicationsRequest(final boolean enable, final BluetoothGattCharacteristic characteristic) {
        return new Request(Type.ENABLE_INDICATIONS, characteristic, enable);
    }


    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public byte[] getBytes() {
        return mBytes;
    }

    public boolean isEnable() {
        return enable;
    }
}
