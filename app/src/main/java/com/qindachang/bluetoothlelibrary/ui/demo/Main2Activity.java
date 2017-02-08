package com.qindachang.bluetoothlelibrary.ui.demo;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.qindachang.bluetoothle.BluetoothLe;
import com.qindachang.bluetoothle.OnLeConnectListener;
import com.qindachang.bluetoothle.OnLeNotificationListener;
import com.qindachang.bluetoothle.exception.BleException;
import com.qindachang.bluetoothle.exception.ConnBleException;
import com.qindachang.bluetoothlelibrary.BluetoothUUID;
import com.qindachang.bluetoothlelibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个示例将会告诉你，如何在activity与多个fragment中使用该库
 *
 * */

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = Main2Activity.class.getSimpleName();

    private BluetoothLe mBluetoothLe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ViewPager vpMain2 = (ViewPager) findViewById(R.id.vp_main2);
        setTitle("使用示例");

        List<Fragment> fragmentList = new ArrayList<>();
        OneFragment oneFragment = new OneFragment();
        TwoFragment twoFragment = new TwoFragment();
        fragmentList.add(oneFragment);
        fragmentList.add(twoFragment);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        vpMain2.setAdapter(pagerAdapter);

        mBluetoothLe = BluetoothLe.getDefault();

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
                //开启电池电量通知
                mBluetoothLe.enableNotification(true, BluetoothUUID.BATTERY_SERVICE, BluetoothUUID.BATTERY_NOTIFICATION);
            }

            @Override
            public void onDeviceConnectFail(ConnBleException e) {

            }
        });

        //监听电池电量通知
        mBluetoothLe.setOnNotificationListener(TAG, new OnLeNotificationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                if (characteristic.getUuid().equals(BluetoothUUID.BATTERY_NOTIFICATION)) {
                    setTitle("使用示例" + "     电量：" + characteristic.getValue()[0] + "%");
                }
            }

            @Override
            public void onFailed(BleException e) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLe.destroy(TAG);
        mBluetoothLe.close();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;
        MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragmentList = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

}
