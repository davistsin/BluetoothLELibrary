package com.qindachang.bluetoothlelibrary.ui.demo;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qindachang.bluetoothle.BluetoothLe;
import com.qindachang.bluetoothle.OnLeConnectListener;
import com.qindachang.bluetoothle.OnLeNotificationListener;
import com.qindachang.bluetoothle.exception.BleException;
import com.qindachang.bluetoothle.exception.ConnBleException;
import com.qindachang.bluetoothlelibrary.R;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends Fragment {
    private static final String TAG = TwoFragment.class.getSimpleName();

    private TextView tvStatus;
    private TextView tvNotification;

    private BluetoothLe mBluetoothLe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_two, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvStatus = (TextView) view.findViewById(R.id.tv_fragment_two_status);
        tvNotification = (TextView) view.findViewById(R.id.tv_fragment_two_text);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothLe.destroy(TAG);
    }
}
