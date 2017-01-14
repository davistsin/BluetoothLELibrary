package com.qindachang.bluetoothlelibrary;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qindachang.bluetoothle.BluetoothLe;
import com.qindachang.bluetoothle.OnLeConnectListener;
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

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * 以下uuid仅作为作者本人实际硬件的使用，请你更换为你所使用的uuid，如果不明白，可以去询问你的硬件工程师
     **/
    private static final String SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca0e";
    private static final String HEART_NOTIFICATION_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca0e";
    private static final String STEP_NOTIFICATION_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca0e";
    private static final String WRITE_UUID = "6e400006-b5a3-f393-e0a9-e50e24dcca0e";
    private static final String READ_UUID = "6e400007-b5a3-f393-e0a9-e50e24dcca0e";

    private static final byte[] OPEN_HEART_RATE_NOTIFY = {0x01, 0x01};
    private static final byte[] OPEN_STEP_NOTIFY = {0x06, 0x01};

    @BindView(R.id.tv_text)
    TextView mTvText;

    private BluetoothLe mBluetoothLe;
    private StringBuilder mStringBuilder;
    private Handler mHandler = new Handler();

    private BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mBluetoothLe = BluetoothLe.getDefault();//获取单例对象
        mStringBuilder = new StringBuilder();

        if (!mBluetoothLe.isBluetoothOpen()) {
            mBluetoothLe.enableBluetooth(this);
        }

        mBluetoothLe.setOnConnectListener(TAG, new OnLeConnectListener() {
            @Override
            public void onDeviceConnecting() {
                mStringBuilder.append("连接中...\n");
                mTvText.setText(mStringBuilder.toString());
            }

            @Override
            public void onDeviceConnected() {
                mStringBuilder.append("蓝牙已连接\n");
                mTvText.setText(mStringBuilder.toString());

            }


            @Override
            public void onDeviceDisconnected() {
                mStringBuilder.append("蓝牙已断开！\n");
                mTvText.setText(mStringBuilder.toString());
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt) {
                mStringBuilder.append("发现服务啦\n");
                mTvText.setText(mStringBuilder.toString());

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //查看远程蓝牙设备的连接参数，如蓝牙的最小时间间隔和最大时间间隔等..
                        Log.d("debug", mBluetoothLe.readConnectionParameters().toString());
                    }
                }, 1000);
            }

            @Override
            public void onDeviceConnectFail(ConnBleException e) {
                mStringBuilder.append("连接失败~~\n").append(e.toString());
                mTvText.setText(mStringBuilder.toString());
            }
        });

        mBluetoothLe.setOnReadCharacteristicListener(TAG, new OnLeReadCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                mStringBuilder.append("读取到：").append(Arrays.toString(characteristic.getValue()));
                mStringBuilder.append("\n");
                mTvText.setText(mStringBuilder.toString());
            }

            @Override
            public void onFailure(ReadBleException e) {
                mStringBuilder.append("读取失败：").append(e.toString());
                mStringBuilder.append("\n");
                mTvText.setText(mStringBuilder.toString());
            }
        });


        mBluetoothLe.setOnWriteCharacteristicListener(TAG, new OnLeWriteCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                mStringBuilder.append("发送了：").append(Arrays.toString(characteristic.getValue()));
                mStringBuilder.append("\n");
                mTvText.setText(mStringBuilder.toString());
            }

            @Override
            public void onFailed(WriteBleException e) {
                mStringBuilder.append("写入数据错误：\n").append(e.toString());
                mStringBuilder.append("\n");
                mTvText.setText(mStringBuilder.toString());
            }
        });

        mBluetoothLe.setOnNotificationListener(TAG, new OnLeNotificationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                mStringBuilder.append("收到通知：")
                        .append(Arrays.toString(characteristic.getValue()))
                        .append("\n");
                mTvText.setText(mStringBuilder.toString());
            }

            @Override
            public void onFailed(BleException e) {
                mStringBuilder.append("开启通知失败:\n")
                        .append(e.toString());
                mTvText.setText(mStringBuilder.toString());
            }
        });

        mBluetoothLe.setReadRssiInterval(2000)
                .setOnReadRssiListener(TAG, new OnLeReadRssiListener() {
                    @Override
                    public void onSuccess(int rssi, int cm) {
                        Log.d("debug", "信号强度：" + rssi + "  距离：" + cm + "cm");
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //为避免内存泄露
        mBluetoothLe.destroy();//使用了没有tag的监听，例如下方232行的扫描监听，这时需要调用destroy();
        mBluetoothLe.destroy(TAG);//如果使用的tag的回调监听，例如上面66行的连接、读取、写等，需要调用destroy(TAG);

        mBluetoothLe.close();//关闭GATT连接，在你退出应用时使用
    }

    @OnClick({R.id.btn_stop_scan, R.id.btn_disconnect, R.id.btn_clear_text, R.id.btn_write_hr,
            R.id.btn_write_step, R.id.btn_read, R.id.btn_connect, R.id.btn_scan, R.id.btn_open_notification,
            R.id.btn_clear_queue, R.id.btn_close_notification, R.id.btn_clear_cache})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                scan();
                break;
            case R.id.btn_stop_scan:
                stopScan();
                break;
            case R.id.btn_connect:
                connect();
                break;
            case R.id.btn_disconnect:
                disconnect();
                break;
            case R.id.btn_clear_text:
                mStringBuilder.delete(0, mStringBuilder.length());
                mTvText.setText("");
                break;
            case R.id.btn_read:
                read();
                break;
            case R.id.btn_write_hr:
                //试一下连发50条
                Toast.makeText(this, "1111", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < 50; i++) {
                    sendMsg(OPEN_HEART_RATE_NOTIFY);
                }
                break;
            case R.id.btn_write_step:
                sendMsg(OPEN_STEP_NOTIFY);
                break;
            case R.id.btn_open_notification:
                openAllNotification();
                break;
            case R.id.btn_close_notification:
                closeAllNotification();
                break;
            case R.id.btn_clear_cache:
                clearCache();
                break;
            case R.id.btn_clear_queue:
                mBluetoothLe.clearQueue();
                break;
        }
    }

    //扫描兼容了4.3/5.0/6.0的安卓版本
    //在6.0版本中，蓝牙扫描需要地理位置权限，否则会出现没有扫描结果的情况，扫描程序中已自带权限申请
    private void scan() {
        //先判断蓝牙是否正在扫描，如果是则先停止扫描，否则会报错
        if (mBluetoothLe.getScanning()) {
            mBluetoothLe.stopScan();
        }
        mBluetoothLe.setScanPeriod(25000)//设置扫描时长，单位毫秒,默认10秒
                //               .setScanWithDeviceAddress("C2:53:32:7C:00:30")
                //               .setScanWithServiceUUID(new String[]{"0000180d-0000-1000-8000-00805f9b34fb","6E400001-B5A3-F393-E0A9-E50E24DCCA9E"})//设置根据服务uuid过滤扫描

                 .setScanWithDeviceName("SmartBra")//设置根据设备名称过滤扫描
                .setReportDelay(0)//如果为0，则回调onScanResult()方法，如果大于0, 则每隔你设置的时长回调onBatchScanResults()方法，不能小于0
                .startScan(this, new OnLeScanListener() {
                    @Override
                    public void onScanResult(BluetoothDevice bluetoothDevice, int rssi, ScanRecord scanRecord) {
                        mStringBuilder.append("扫描到设备：").append(bluetoothDevice.getName()).append("-信号强度：").append(rssi).append("\n");
                        mTvText.setText(mStringBuilder.toString());
                        mBluetoothDevice = bluetoothDevice;
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        mStringBuilder.append("批处理信息：").append(results.toString()).append("\n");
                        mTvText.setText(mStringBuilder.toString());
                    }

                    @Override
                    public void onScanCompleted() {
                        mStringBuilder.append("扫描结束\n");
                        mTvText.setText(mStringBuilder.toString());
                    }

                    @Override
                    public void onScanFailed(ScanBleException e) {
                        mStringBuilder.append("扫描错误\n" + e.toString());
                        mTvText.setText(mStringBuilder.toString());
                    }
                });
    }

    private void stopScan() {
        mBluetoothLe.stopScan();
        mStringBuilder.append("停止扫描\n");
        mTvText.setText(mStringBuilder.toString());
    }

    //有些手机的连接过程比较长，经测试，小米手机连接所花时间最多，有时会在15s左右
    //发送数据、开启通知等操作，必须等待onServicesDiscovered()发现服务回调后，才能去操作
    private void connect() {
        mBluetoothLe.startConnect(false, mBluetoothDevice);
    }

    private void disconnect() {
        mBluetoothLe.disconnect();
    }

    private void openAllNotification() {
        mBluetoothLe.enableNotification(true, SERVICE_UUID, new String[]{HEART_NOTIFICATION_UUID, STEP_NOTIFICATION_UUID});
    }

    private void closeAllNotification() {
        mBluetoothLe.enableNotification(false, SERVICE_UUID, new String[]{HEART_NOTIFICATION_UUID,STEP_NOTIFICATION_UUID});
    }

    private void sendMsg(byte[] bytes) {
        mBluetoothLe.writeDataToCharacteristic(bytes, SERVICE_UUID, WRITE_UUID);
    }

    private void read() {
        mBluetoothLe.readCharacteristic(SERVICE_UUID, READ_UUID);
    }

    private void clearCache() {
        if (!mBluetoothLe.getConnected()) {
            return;
        }
        if (mBluetoothLe.clearDeviceCache()) {
            mStringBuilder.append("清理蓝牙缓存：成功");

        } else {
            mStringBuilder.append("清理蓝牙缓存：失败！");
        }
        mStringBuilder.append("\n");
        mTvText.setText(mStringBuilder.toString());
    }

}
