package com.qindachang.bluetoothlelibrary;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qindachang.bluetoothle.BluetoothLe;
import com.qindachang.bluetoothle.OnLeConnectListener;
import com.qindachang.bluetoothle.OnLeIndicationListener;
import com.qindachang.bluetoothle.OnLeNotificationListener;
import com.qindachang.bluetoothle.OnLeReadCharacteristicListener;
import com.qindachang.bluetoothle.OnLeReadRssiListener;
import com.qindachang.bluetoothle.OnLeScanListener;
import com.qindachang.bluetoothle.OnLeWriteCharacteristicListener;
import com.qindachang.bluetoothle.exception.BleException;
import com.qindachang.bluetoothle.exception.ConnBleException;
import com.qindachang.bluetoothle.exception.ReadBleException;
import com.qindachang.bluetoothle.exception.ScanBleException;
import com.qindachang.bluetoothle.exception.WriteBleException;
import com.qindachang.bluetoothle.scanner.ScanRecord;
import com.qindachang.bluetoothle.scanner.ScanResult;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private BluetoothLe mBluetoothLe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothLe = BluetoothLe.getDefault();

        if (!mBluetoothLe.isSupportBluetooth()) {
            //设备不支持蓝牙
        } else {
            if (!mBluetoothLe.isBluetoothOpen()) {
                //没有打开蓝牙，请求打开手机蓝牙
                mBluetoothLe.enableBluetooth(this, 666);
            }
        }

        //监听蓝牙回调
        //监听扫描
        mBluetoothLe.setOnScanListener(TAG, new OnLeScanListener() {
            @Override
            public void onScanResult(BluetoothDevice bluetoothDevice, int rssi, ScanRecord scanRecord) {

            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {

            }

            @Override
            public void onScanCompleted() {

            }

            @Override
            public void onScanFailed(ScanBleException e) {

            }
        });
        //监听连接
        mBluetoothLe.setOnConnectListener(TAG, new OnLeConnectListener() {
            @Override
            public void onDeviceConnecting() {

            }

            @Override
            public void onDeviceConnected() {

            }

            @Override
            public void onDeviceDisconnected() {

            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt) {

            }

            @Override
            public void onDeviceConnectFail(ConnBleException e) {

            }
        });
        //监听通知，类型notification
        mBluetoothLe.setOnNotificationListener(TAG, new OnLeNotificationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {

            }

            @Override
            public void onFailed(BleException e) {

            }
        });
        //监听通知，类型indicate
        mBluetoothLe.setOnIndicationListener(TAG, new OnLeIndicationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {

            }

            @Override
            public void onFailed(BleException e) {

            }
        });
        //监听写
        mBluetoothLe.setOnWriteCharacteristicListener(TAG, new OnLeWriteCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {

            }

            @Override
            public void onFailed(WriteBleException e) {

            }
        });
        //监听读
        mBluetoothLe.setOnReadCharacteristicListener(TAG, new OnLeReadCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {

            }

            @Override
            public void onFailure(ReadBleException e) {

            }
        });
        //监听信号强度
        mBluetoothLe.setOnReadRssiListener(TAG, new OnLeReadRssiListener() {
            @Override
            public void onSuccess(int rssi, int cm) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && resultCode == RESULT_OK) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销监听，避免内存泄露
        mBluetoothLe.destroy(TAG);
        //关闭GATT
        mBluetoothLe.close();
    }
}
