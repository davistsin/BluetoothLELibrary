package com.qindachang.bluetoothlelibrary.ui.demo;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qindachang.bluetoothle.BluetoothLe;
import com.qindachang.bluetoothle.OnLeConnectListener;
import com.qindachang.bluetoothle.OnLeNotificationListener;
import com.qindachang.bluetoothle.OnLeScanListener;
import com.qindachang.bluetoothle.exception.BleException;
import com.qindachang.bluetoothle.exception.ConnBleException;
import com.qindachang.bluetoothle.exception.ScanBleException;
import com.qindachang.bluetoothle.scanner.ScanRecord;
import com.qindachang.bluetoothle.scanner.ScanResult;
import com.qindachang.bluetoothlelibrary.BluetoothUUID;
import com.qindachang.bluetoothlelibrary.R;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneFragment extends Fragment {
    private static final String TAG = OneFragment.class.getSimpleName();

    private BluetoothLe mBluetoothLe;

    private Button btnScanConnect;
    private TextView tvStatus;
    private TextView tvNotification;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnScanConnect = (Button) view.findViewById(R.id.btn_fragment_one_connect);
        tvStatus = (TextView) view.findViewById(R.id.tv_fragment_one_status);
        tvNotification = (TextView) view.findViewById(R.id.tv_fragment_one_text);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBluetoothLe = BluetoothLe.getDefault();

        mBluetoothLe.setOnConnectListener(TAG, new OnLeConnectListener() {
            @Override
            public void onDeviceConnecting() {
                tvStatus.setText("连接中...");
            }

            @Override
            public void onDeviceConnected() {
                tvStatus.setText("已连接");
            }

            @Override
            public void onDeviceDisconnected() {
                tvStatus.setText("断开连接");
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt) {
                //连接蓝牙发现服务成功后，开启两个通知
                mBluetoothLe.enableNotification(true, BluetoothUUID.SERVICE,
                        new UUID[]{BluetoothUUID.HR_NOTIFICATION, BluetoothUUID.STEP_NOTIFICATION});
            }

            @Override
            public void onDeviceConnectFail(ConnBleException e) {

            }
        });

        mBluetoothLe.setOnNotificationListener(TAG, new OnLeNotificationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                tvNotification.setText("收到通知：\n" + Arrays.toString(characteristic.getValue()));
            }

            @Override
            public void onFailed(BleException e) {

            }
        });

        btnScanConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanAndConnect();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销没有TAG的监听，例如下面scanAndConnect()方法中的OnLeScanListener监听
        mBluetoothLe.destroy();
        //根据TAG注销监听，避免内存泄露，例如上方的通知回调监听
        mBluetoothLe.destroy(TAG);
    }

    private void scanAndConnect() {
        mBluetoothLe.setScanWithServiceUUID(BluetoothUUID.SERVICE)
                .setScanPeriod(15000)
                .startScan(getActivity(), new OnLeScanListener() {
                    @Override
                    public void onScanResult(BluetoothDevice bluetoothDevice, int rssi, ScanRecord scanRecord) {
                        mBluetoothLe.stopScan();
                        mBluetoothLe.startConnect(bluetoothDevice);
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
    }
}
