package com.qindachang.bluetoothle;

import android.bluetooth.BluetoothDevice;

import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;


public abstract class OnLeScanListener extends LeListener {
    public abstract void onScanResult(BluetoothDevice bluetoothDevice, int rssi, ScanRecord scanRecord);

    public abstract void onBatchScanResults(List<ScanResult> results);

    public abstract void onScanCompleted();

    public abstract void onScanFailed(int code);
}
