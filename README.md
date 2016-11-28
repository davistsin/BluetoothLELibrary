#BluetoothLE library

[English](https://github.com/qindachang/BluetoothLELibrary/blob/master/README-EN.md "English")

低功耗蓝牙库。**优势**：

1. **适配**到Android5.0和Android6.0的扫描方式（速度极快）。
2. 适配小米手机连接蓝牙操作。
3. 适配三星手机发现服务、开启通知等。
4. 支持**直接连发数百条**数据，而不用担心消息发不出。自带消息队列（终于可以像iOS一样啦，不用去写延时啦）。
5. 支持同时**开启多个通知**。
6. 可以连续操作发送数据、读取特征、开启通知，即使你在for循环中写也没问题，**自带队列**。
6. 扫描操作支持-> 设置扫描时长、根据设备名称扫描、根据硬件地址扫描、根据服务UUID扫描、连接成功后自动关闭扫描。


###注意点：
1. Android 6.0扫描蓝牙需要地理位置权限。
2. 发送数据、开启通知、读取特征等操作，需要在onServicesDiscovered()发现服务之后才能进行。
3. 连接设备之前最好先停止扫描（小米手机可能会出现不能发现服务的情况）。

##入门指南

权限：

    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    <uses-permission android:name="android.permission.BLUETOOTH_PRIVILEGED"/>

6.0以上设备需要

    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

**引入方式**

	compile 'com.qindachang:BluetoothLELibrary:0.3.1'


**前戏**

判断蓝牙是否打开

    mBluetoothLe.isBluetoothOpen();

请求打开蓝牙

    mBluetoothLe.enableBluetooth(activity.this);

关闭蓝牙

    mBluetoothLe.disableBluetooth();


**一、获取单例实例**

	BluetoothLe mBluetoothLe = BluetoothLe.getDefault();

**二、初始化**

	mBluetoothLe.init(this);//必须调用init()初始化

**三、扫描**

扫描过程已携带6.0动态权限申请：地理位置权限

    private BluetoothDevice mBluetoothDevice;

    mBluetoothLe.setScanPeriod(15000)//设置扫描时长，单位毫秒，默认10秒
                .setScanWithServiceUUID("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")//设置根据服务uuid过滤扫描
                .setScanWithDeviceName("ZG1616")//设置根据设备名称过滤扫描
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

由于使用了单例，单例的生命长度与APP的一致，比activity长。当activity应被回收时，为避免单例的listener回调持有引用，导致activity不能正常被回收，从而引发内存泄露，
所以在每一个listener前增加一个tag标志，类似于volley，在onDestroy()方法中取消掉对应的tag，可避免内存泄露。


    mBluetoothLe.setScanPeriod(15000)//设置扫描时长，单位毫秒，默认10秒
                .setScanWithServiceUUID("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")//设置根据服务uuid过滤扫描
                .setScanWithDeviceName("ZG1616")//设置根据设备名称过滤扫描
                .setReportDelay(0)//如果为0，则回调onScanResult()方法，如果大于0, 则每隔你设置的时长回调onBatchScanResults()方法，不能小于0
                .startScan(TAG, this, new OnLeScanListener() {
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


获取蓝牙扫描状态：

    mBluetoothLe.getScanning();


**四、停止扫描**

    mBluetoothLe.stopScan();

**五、连接蓝牙**

	//发送数据、开启通知等操作，必须等待onServicesDiscovered()发现服务回调后，才能去操作
	//参数：false为关闭蓝牙自动重连，如果为true则自动重连
    mBluetoothLe.startConnect(false, mBluetoothDevice, new OnLeConnectListener() {

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

使用tag：监听连接

    mBluetoothLe.startConnect(false, mBluetoothDevice);

    mBluetoothLe.setOnConnectListener(TAG, new OnLeConnectListener() {
         @Override
         public void onDeviceConnecting() {
                    mStringBuilder.append("连接中1");
                    mStringBuilder.append("\n");
                    tv_text.setText(mStringBuilder.toString());
         }

         @Override
         public void onDeviceConnected() {
                    mStringBuilder.append("已连接1");
                    mStringBuilder.append("\n");
                    tv_text.setText(mStringBuilder.toString());
                }

         @Override
         public void onDeviceDisconnected() {
                    mStringBuilder.append("断开连接1");
                    mStringBuilder.append("\n");
                    tv_text.setText(mStringBuilder.toString());
         }

         @Override
         public void onServicesDiscovered(BluetoothGatt gatt) {
                    mStringBuilder.append("发现服务1");
                    mStringBuilder.append("\n");
                    tv_text.setText(mStringBuilder.toString());
         }

         @Override
         public void onDeviceConnectFail() {
                    mStringBuilder.append("连接失败1");
                    mStringBuilder.append("\n");
                    tv_text.setText(mStringBuilder.toString());
                }
    });

获取蓝牙连接状态：

    mBluetoothLe.getConnected();


**断开连接**

    mBluetoothLe.disconnect();

**五、发送数据（到蓝牙特征）**

	//以下两个参数为硬件工程师提供，请你与你司的硬件工程师沟通
    private static final String SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    private static final String WRITE_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";

    mBluetoothLe.writeDataToCharacteristic(bytes, SERVICE_UUID, WRITE_UUID);

**六、监听发送数据**

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

使用tag：

    mBluetoothLe.setOnWriteCharacteristicListener(TAG, new OnLeWriteCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {

            }

            @Override
            public void onFailed(String msg, int status) {

            }
        });

**七、开启通知**

	private static final String SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    private static final String HEART_NOTIFICATION_UUID = "00002a37-0000-1000-8000-00805f9b34fb";
    private static final String STEP_NOTIFICATION_UUID = "0000fff3-0000-1000-8000-00805f9b34fb";

	mBluetoothLe.enableNotification(true, SERVICE_UUID, STEP_NOTIFICATION_UUID);

**八、开启多个通知**

    mBluetoothLe.enableNotification(true, SERVICE_UUID, new String[]{HEART_NOTIFICATION_UUID, STEP_NOTIFICATION_UUID});

**九、监听通知**

    mBluetoothLe.setOnNotificationListener(new OnLeNotificationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                Log.d("debug", "收到通知：" + Arrays.toString(characteristic.getValue()));
            }

    });

使用tag：

    mBluetoothLe.setOnNotificationListener(TAG, new OnLeNotificationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                Log.d("debug", "收到通知：" + Arrays.toString(characteristic.getValue()));
            }

    });

**十、读取数据**

    private static final String SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    private static final String READ_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";

    mBluetoothLe.readCharacteristic(SERVICE_UUID, READ_UUID);

监听读取：

    mBluetoothLe.setOnReadCharacteristicListener(new OnLeReadCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                
            }

            @Override
            public void onFailure(String info, int status) {

            }
    );

使用tag：

    mBluetoothLe.setOnReadCharacteristicListener(TAG, new OnLeReadCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {

            }

            @Override
            public void onFailure(String info, int status) {

            }
    );


**十一、取消队列**

假设当你在for循环中发送100条数据，想要在中途取消余下的发送

    mBluetoothLe.clearQueue();

**十二、关闭GATT**

在你退出应用的时候使用

    mBluetoothLe.close();

**十三、清理蓝牙缓存**

请你在连接上蓝牙后，再执行这步操作

    mBluetoothLe.clearDeviceCache()


###避免内存泄露

在Activity生命周期onDestroy() 中使用：

	mBluetoothLe.destroy();

如果你使用了tag，使用: （它的好处是你可以在多个界面产生多个相同的回调）

    mBluetoothLe.destroy(TAG);

取消对应tag：

    mBluetoothLe.cancelTag(TAG);

取消全部tag：

    mBluetoothLe.cancelAllTag();

##仍在补充
1. 连续操作发送数据、读取特征、开启通知操作设置优先级，像网络请求一样设置优先级
2. 蓝牙设备信号强度监听
3. 连接超时设置，连接不上的情况下自动重连的次数设置

##了解更多

1. See Demo： [MainActivity.java](https://github.com/qindachang/BluetoothLELibrary/blob/master/app/src/main/java/com/qindachang/bluetoothlelibrary/MainActivity.java "MainActivity.java") / [activity_main.xml](https://github.com/qindachang/BluetoothLELibrary/blob/master/app/src/main/res/layout/activity_main.xml "activity_main.xml")
2. QQ: 714275846 / 823951895
3. 邮箱：qindachang@outlook.com
4. 博客：http://blog.csdn.net/u013003052
5. Github: https://github.com/qindachang

##版本迭代
1. [Version 0.1.0](https://github.com/qindachang/BluetoothLELibrary/blob/master/document/version-0.1.0.md "Version 0.1.0")
2. [Version 0.1.1](https://github.com/qindachang/BluetoothLELibrary/blob/master/document/version-0.1.1.md "Version 0.1.1")

    增加：取消所有队列

3. [Version 0.2.0](https://github.com/qindachang/BluetoothLELibrary/blob/master/document/version-0.2.0.md "Version 0.2.0")

    增加：

    清理蓝牙缓存；
    判断蓝牙是否打开；
    请求打开蓝牙。

4. [Version 0.2.1] 增加获取蓝牙连接状态。
5. [Version 0.2.2] fix bug.
6. [Version 0.3.0](https://github.com/qindachang/BluetoothLELibrary/blob/master/document/version-0.3.0.md "Version 0.3.0")

    增加：类似volley的TAG，可以取消对应TAG的监听，避免内存泄露。
