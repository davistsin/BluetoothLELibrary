package com.qindachang.bluetoothle;

import android.bluetooth.BluetoothDevice;

import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;


public interface OnLeScanListener {
    void onScanResult(BluetoothDevice bluetoothDevice,int rssi,ScanRecord scanRecord);

    void onBatchScanResults(List<ScanResult> results);

    void onScanCompleted();

    void onScanFailed(int code);
}
