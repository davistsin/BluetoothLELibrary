package com.qindachang.bluetoothlelibrary;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.qindachang.bluetoothle.BluetoothConfig;
import com.qindachang.bluetoothle.BluetoothLe;
import com.qindachang.bluetoothle.OnLeConnectListener;
import com.qindachang.bluetoothle.OnLeNotificationListener;
import com.qindachang.bluetoothle.OnLeReadCharacteristicListener;
import com.qindachang.bluetoothle.OnLeRssiListener;
import com.qindachang.bluetoothle.OnLeScanListener;
import com.qindachang.bluetoothle.OnLeWriteCharacteristicListener;

import java.util.Arrays;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    private static final String HEART_NOTIFICATION_UUID = "00002a37-0000-1000-8000-00805f9b34fb";
    private static final String STEP_NOTIFICATION_UUID = "0000fff3-0000-1000-8000-00805f9b34fb";
    private static final String WRITE_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";
    private static final String READ_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";

    private static final byte[] OPEN_HEART_RATE_NOTIFY = {0x01, 0x01};
    private static final byte[] OPEN_STEP_NOTIFY = {0x06, 0x01};

    private TextView tv_text;

    private BluetoothLe mBluetoothLe;
    private StringBuilder mStringBuilder;

    private BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_text = (TextView) findViewById(R.id.tv_text);

        mBluetoothLe = BluetoothLe.getDefault();//获取单例对象
        mBluetoothLe.init(this);//必须调用init()初始化

        mStringBuilder = new StringBuilder();

        if (!mBluetoothLe.isBluetoothOpen()) {
            mBluetoothLe.enableBluetooth(this);
        }

        BluetoothConfig config = new BluetoothConfig.Builder()
                .enableQueueInterval(false)
                .build();
        mBluetoothLe.changeConfig(config);

        mBluetoothLe.setOnConnectListener(TAG, new OnLeConnectListener() {
            @Override
            public void onDeviceConnecting() {
                mStringBuilder.append("连接中...\n");
                tv_text.setText(mStringBuilder.toString());
            }

            @Override
            public void onDeviceConnected() {
                mStringBuilder.append("蓝牙已连接\n");
                tv_text.setText(mStringBuilder.toString());
            }


            @Override
            public void onDeviceDisconnected() {
                mStringBuilder.append("蓝牙已断开！\n");
                tv_text.setText(mStringBuilder.toString());
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt) {
                mStringBuilder.append("发现服务啦\n");
                tv_text.setText(mStringBuilder.toString());
            }

            @Override
            public void onDeviceConnectFail() {
                mStringBuilder.append("连接失败~~\n");
                tv_text.setText(mStringBuilder.toString());
            }
        });

        mBluetoothLe.setOnReadCharacteristicListener(TAG, new OnLeReadCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                mStringBuilder.append("读取到：").append(Arrays.toString(characteristic.getValue()));
                mStringBuilder.append("\n");
                tv_text.setText(mStringBuilder.toString());
            }

            @Override
            public void onFailure(String info, int status) {
                mStringBuilder.append("读取失败：").append(info);
                mStringBuilder.append("\n");
                tv_text.setText(mStringBuilder.toString());
            }
        });


        mBluetoothLe.setOnWriteCharacteristicListener(TAG, new OnLeWriteCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                mStringBuilder.append("发送了：").append(Arrays.toString(characteristic.getValue()));
                mStringBuilder.append("\n");
                tv_text.setText(mStringBuilder.toString());
            }

            @Override
            public void onFailed(String msg, int status) {
                mStringBuilder.append("写入数据错误：").append(msg);
                mStringBuilder.append("\n");
                tv_text.setText(mStringBuilder.toString());
            }
        });

        mBluetoothLe.setOnNotificationListener(TAG, new OnLeNotificationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                mStringBuilder.append("收到通知：")
                        .append(Arrays.toString(characteristic.getValue()))
                        .append("\n");
                tv_text.setText(mStringBuilder.toString());
            }
        });

        mBluetoothLe.setOnRssiListener(TAG, new OnLeRssiListener() {
            @Override
            public void onSuccess(int rssi, int cm) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLe.destroy(TAG);//由于使用了单例，为避免回调及context持有而产生内存泄露，你需要调用destroy()
        mBluetoothLe.close();//关闭GATT连接，在destroy()后使用
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
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
                tv_text.setText("");
                break;
            case R.id.btn_read:
                read();
                break;
            case R.id.btn_write_hr:
                //试一下连发50条
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
        mBluetoothLe.setScanPeriod(15000)//设置扫描时长，单位毫秒,默认10秒
//                .setScanWithDeviceAddress("00:20:ff:34:aa:b3")
//                .setScanWithServiceUUID("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")//设置根据服务uuid过滤扫描
//                 .setScanWithDeviceName("ZG1616")//设置根据设备名称过滤扫描
                .setReportDelay(0)//如果为0，则回调onScanResult()方法，如果大于0, 则每隔你设置的时长回调onBatchScanResults()方法，不能小于0
                .startScan(this, new OnLeScanListener() {
                    @Override
                    public void onScanResult(BluetoothDevice bluetoothDevice, int rssi, ScanRecord scanRecord) {
                        mStringBuilder.append("扫描到设备：" + bluetoothDevice.getName() + "-信号强度：" + rssi + "\n");
                        tv_text.setText(mStringBuilder.toString());
                        mBluetoothDevice = bluetoothDevice;
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        mStringBuilder.append("批处理信息：" + results.toString() + "\n");
                        tv_text.setText(mStringBuilder.toString());
                    }

                    @Override
                    public void onScanCompleted() {
                        mStringBuilder.append("扫描结束\n");
                        tv_text.setText(mStringBuilder.toString());
                    }

                    @Override
                    public void onScanFailed(int code) {
                        mStringBuilder.append("扫描错误\n");
                        tv_text.setText(mStringBuilder.toString());
                    }
                });
    }

    private void stopScan() {
        mBluetoothLe.stopScan();
        mStringBuilder.append("停止扫描\n");
        tv_text.setText(mStringBuilder.toString());
    }

    //有些手机的连接过程比较长，经测试，小米手机连接所花时间最多，有时会在15s左右
    //发送数据、开启通知等操作，必须等待onServicesDiscovered()发现服务回调后，才能去操作
    private void connect() {
        mBluetoothLe.setRetryConnectEnable(false)//设置尝试重新连接
//                .setRetryConnectCount(3)//重试连接次数
//                .setConnectTimeOut(5000)//连接超时，单位毫秒
//                .setServiceDiscoverTimeOut(5000)//发现服务超时，单位毫秒
                .startConnect(false, mBluetoothDevice);
    }

    private void disconnect() {
        mBluetoothLe.disconnect();
    }

    private void openAllNotification() {
        if (mBluetoothLe.getServicesDiscovered()) {
            mBluetoothLe.enableNotification(true, SERVICE_UUID,
                    new String[]{HEART_NOTIFICATION_UUID, STEP_NOTIFICATION_UUID});
        }
    }

    private void closeAllNotification() {
        if (mBluetoothLe.getServicesDiscovered()) {
            mBluetoothLe.enableNotification(false, SERVICE_UUID,
                    new String[]{HEART_NOTIFICATION_UUID, STEP_NOTIFICATION_UUID});
        }
    }

    private void sendMsg(byte[] bytes) {
        if (mBluetoothLe.getServicesDiscovered()) {
            mBluetoothLe.writeDataToCharacteristic(bytes, SERVICE_UUID, WRITE_UUID);
        }
    }

    private void read() {
        if (mBluetoothLe.getServicesDiscovered()) {
            mBluetoothLe.readCharacteristic(SERVICE_UUID, READ_UUID);
        }
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
        tv_text.setText(mStringBuilder.toString());
    }
}
