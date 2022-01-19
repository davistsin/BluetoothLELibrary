package com.davistsin.bluetoothlelibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

import com.davistsin.bluetoothle.main.connect.BleConnectCreator;
import com.davistsin.bluetoothle.main.connect.BleConnector;
import com.davistsin.bluetoothle.main.connect.ConnectorSettings;
import com.davistsin.bluetoothle.main.listener.BleConnectionListener;
import com.davistsin.bluetoothle.main.listener.BleDiscoverServicesListener;
import com.davistsin.bluetoothle.main.listener.BleIndicationListener;
import com.davistsin.bluetoothle.main.listener.BleNotificationListener;
import com.davistsin.bluetoothle.main.listener.BleReadCharacteristicListener;
import com.davistsin.bluetoothle.main.listener.BleWriteCharacteristicListener;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class MainActivity extends AppCompatActivity {
    private String mUuid = "";
    private BluetoothLeScannerCompat mScannerCompat;

    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice bluetoothDevice = result.getDevice();
        }

        @Override
        public void onBatchScanResults(@NonNull List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScannerCompat = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(5000)
                .setUseHardwareBatchingIfSupported(true)
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().build());
        mScannerCompat.startScan(filters, settings, mScanCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerCompat.stopScan(mScanCallback);
    }
}