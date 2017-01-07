package com.qindachang.bluetoothle;

import android.bluetooth.BluetoothDevice;

import com.qindachang.bluetoothle.scanner.ScanRecord;
import com.qindachang.bluetoothle.scanner.ScanResult;

import java.util.List;


public abstract class OnLeScanListener extends LeListener {
    public abstract void onScanResult(BluetoothDevice bluetoothDevice, int rssi, ScanRecord scanRecord);

    public abstract void onBatchScanResults(List<ScanResult> results);

    public abstract void onScanCompleted();

    public abstract void onScanFailed(int code);
}
