package com.qindachang.bluetoothlelibrary;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qindachang.bluetoothle.BluetoothLe;
import com.qindachang.bluetoothle.OnLeConnectListener;
import com.qindachang.bluetoothle.OnLeNotificationListener;
import com.qindachang.bluetoothle.OnLeReadCharacteristicListener;
import com.qindachang.bluetoothle.OnLeScanListener;
import com.qindachang.bluetoothle.OnLeWriteCharacteristicListener;

import java.util.Arrays;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    private static final String HEART_NOTIFICATION_UUID = "00002a37-0000-1000-8000-00805f9b34fb";
    private static final String STEP_NOTIFICATION_UUID = "0000fff3-0000-1000-8000-00805f9b34fb";
    private static final String WRITE_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";
    private static final String READ_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";

    private static final byte[] OPEN_HEART_RATE_NOTIFY = {0x01, 0x01};
    private static final byte[] OPEN_STEP_NOTIFY = {0x06, 0x01};

    private Button btn_scan, btn_stop_scan, btn_connect, btn_disconnect, btn_clear, btn_open_notification,
            btn_write_hr, btn_write_step, btn_read, btn_clear_all, btn_close_all_notify, btn_clear_cache;
    private TextView tv_text;

    private BluetoothLe mBluetoothLe;
    private StringBuilder mStringBuilder;

    private BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_stop_scan = (Button) findViewById(R.id.btn_stop_scan);
        tv_text = (TextView) findViewById(R.id.tv_text);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_open_notification = (Button) findViewById(R.id.btn_open_notification);
        btn_write_hr = (Button) findViewById(R.id.btn_write_hr);
        btn_write_step = (Button) findViewById(R.id.btn_write_step);
        btn_read = (Button) findViewById(R.id.btn_read);
        btn_clear_all = (Button) findViewById(R.id.btn_clear_all);
        btn_close_all_notify = (Button) findViewById(R.id.btn_close_all_notify);
        btn_clear_cache = (Button) findViewById(R.id.btn_clear_cache);

        mBluetoothLe = BluetoothLe.getDefault();//获取单例对象
        mBluetoothLe.init(this);//必须调用init()初始化

        mStringBuilder = new StringBuilder();


        if (!mBluetoothLe.isBluetoothOpen()) {
            mBluetoothLe.enableBluetooth(this);
        }

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });

        btn_stop_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
            }
        });

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStringBuilder.delete(0, mStringBuilder.length());
                tv_text.setText("");
            }
        });

        btn_open_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAllNotification();
            }
        });

        btn_write_hr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 50; i++) {
                    sendMsg(OPEN_HEART_RATE_NOTIFY);
                }
            }
        });
        btn_write_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg(OPEN_STEP_NOTIFY);
            }
        });


        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 20; i++) {
                    read();
                }
            }
        });
        btn_clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothLe.clearQueue();
            }
        });

        btn_close_all_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothLe.enableBleNotification(false, SERVICE_UUID, new String[]{HEART_NOTIFICATION_UUID, STEP_NOTIFICATION_UUID});
            }
        });

        btn_clear_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBluetoothLe.clearDeviceCache()) {
                    mStringBuilder.append("清理蓝牙缓存：成功");

                } else {
                    mStringBuilder.append("清理蓝牙缓存：失败！");
                }
                mStringBuilder.append("\n");
                tv_text.setText(mStringBuilder.toString());
            }
        });

        mBluetoothLe.setOnReadCharacteristicListener(new OnLeReadCharacteristicListener() {
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
        mBluetoothLe.setOnWriteCharacteristicListener(new OnLeWriteCharacteristicListener() {

            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                Log.d("debug", "发送了数据:" + Arrays.toString(characteristic.getValue()));
                mStringBuilder.append("发送了：").append(Arrays.toString(characteristic.getValue()));
                mStringBuilder.append("\n");
                tv_text.setText(mStringBuilder.toString());
            }

            @Override
            public void onFailed(String msg, int status) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLe.destroy();
        mBluetoothLe.destroy(TAG);//由于使用了单例，为避免回调及context持有而产生内存泄露，你需要调用destroy()
        mBluetoothLe.close();//关闭GATT连接，在destroy()后使用
    }

    //扫描兼容了4.3/5.0/6.0的安卓版本
    //在6.0版本中，蓝牙扫描需要地理位置权限，否则会出现没有扫描结果的情况，扫描程序中已自带权限申请
    private void scan() {
        mBluetoothLe.setScanPeriod(15000)//设置扫描时长，单位毫秒
//                .setScanWithServiceUUID("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")//设置根据服务uuid过滤扫描
               // .setScanWithDeviceName("ZG1616")//设置根据设备名称过滤扫描
                .setReportDelay(0)//如果为0，则回调onScanResult()方法，如果大于0, 则每隔你设置的时长回调onBatchScanResults()方法，不能小于0
                .startBleScan(this,TAG, new OnLeScanListener() {
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
        mBluetoothLe.stopBleScan();
        mStringBuilder.append("停止扫描\n");
        tv_text.setText(mStringBuilder.toString());
    }

    //有些手机的连接过程比较长，经测试，小米手机连接所花时间最多，有时会在15s左右
    //发送数据、开启通知等操作，必须等待onServicesDiscovered()发现服务回调后，才能去操作
    private void connect() {
        mBluetoothLe.startBleConnect(false, mBluetoothDevice, new OnLeConnectListener() {

            @Override
            public void onDeviceConnecting() {
                mStringBuilder.append("连接中...\n");
                tv_text.setText(mStringBuilder.toString());
            }

            @Override
            public void onDeviceConnected() {
                mStringBuilder.append("成功连接！\n");
                tv_text.setText(mStringBuilder.toString());
            }


            @Override
            public void onDeviceDisconnected() {
                mStringBuilder.append("断开连接！\n");
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
    }

    private void disconnect() {
        mBluetoothLe.disconnect();
        mBluetoothLe.close();
    }

    private void openNotification() {
        mBluetoothLe.enableBleNotification(true, SERVICE_UUID, STEP_NOTIFICATION_UUID)
                .setOnNotificationListener(new OnLeNotificationListener() {
                    @Override
                    public void onSuccess(BluetoothGattCharacteristic characteristic) {

                    }

                    @Override
                    public void onFailure() {

                    }
                });
    }

    private void openAllNotification() {
        mBluetoothLe.enableBleNotification(true, SERVICE_UUID, new String[]{HEART_NOTIFICATION_UUID, STEP_NOTIFICATION_UUID});
        mBluetoothLe.setOnNotificationListener(new OnLeNotificationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                Log.d("debug", "收到通知：" + Arrays.toString(characteristic.getValue()));
                mStringBuilder.append("收到通知：" + Arrays.toString(characteristic.getValue()) + "\n");
                tv_text.setText(mStringBuilder.toString());
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void closeNotification() {
        mBluetoothLe.enableBleNotification(false, SERVICE_UUID, HEART_NOTIFICATION_UUID);
    }

    private void sendMsg(byte[] bytes) {
        mBluetoothLe.writeDataToCharacteristic(bytes, SERVICE_UUID, WRITE_UUID);
    }

    private void read() {
        mBluetoothLe.readCharacteristic(SERVICE_UUID, READ_UUID);

    }

}
