package com.davistsin.bluetoothle;

import static android.bluetooth.BluetoothProfile.GATT;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import androidx.annotation.RequiresPermission;

import java.util.List;


public class BleHelper {

    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public static List<BluetoothDevice> getConnectedDevices(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager.getConnectedDevices(GATT);
    }

    public static boolean isSupportBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public static boolean isBluetoothEnable() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static boolean disableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.disable();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static boolean enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.enable();
    }
}
