
![title](https://github.com/qindachang/BluetoothLELibrary/blob/master/image/title.jpg)

![JitPack.io](https://img.shields.io/pypi/l/Django.svg)
![Release Version](https://img.shields.io/badge/release-0.7.0-red.svg)

[English](https://github.com/qindachang/BluetoothLELibrary/blob/master/README-EN.md "English") [固件升级/硬件升级/DFU](https://github.com/qindachang/DFUDemo "固件升级/硬件升级/DFU")
[下载jar文件](https://github.com/qindachang/BluetoothLELibrary/blob/master/jars/ "下载jar文件")

该库只支持1对1连接，如果你想1对多设备连接，请移步至
[BluetoothLE-Multi-Library](https://github.com/qindachang/BluetoothLE-Multi-Library "BluetoothLE-Multi-Library")

低功耗蓝牙库。**优势**：

1. **适配**到Android5.0和Android6.0的扫描方式（速度极快）。
2. 适配小米手机连接蓝牙操作。
3. 适配三星手机发现服务、开启通知等。
4. 支持**直接连发数百条**数据，而不用担心消息发不出。自带消息队列（终于可以像iOS一样啦，不用去写延时啦）。
5. 支持同时**开启多个通知**。
6. 可以连续操作发送数据、读取特征、开启通知，即使你在for循环中写也没问题，**自带队列**。
6. 扫描操作支持-> 设置扫描时长、根据设备名称扫描、根据硬件地址扫描、根据服务UUID扫描、连接成功后自动关闭扫描。
7. 队列定时设置，满足因公司需求蓝牙时间间隔。
8. 设备信号强度、距离计算回调，可用于防丢器产品。

###注意点：
1. Android 6.0扫描蓝牙需要地理位置权限。
2. 发送数据、开启通知、读取特征等操作，需要在onServicesDiscovered()发现服务之后才能进行。
3. 连接设备之前最好先停止扫描（小米手机可能会出现不能发现服务的情况）。

##入门指南

**引入方式**

添加依赖

作为第一步,依赖这个库添加到您的项目。如何使用库, Gradle是推荐的方式使用这个库的依赖。

添加以下代码在你的APP级别 app build.gradle:

	compile 'com.qindachang:BluetoothLELibrary:0.7.0'

**权限：**

    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>

5.0以上需要

    <!-- Needed only if your app targets Android 5.0 (API level 21) or higher. -->
    <uses-feature android:name="android.hardware.location.gps" />

6.0以上设备需要

    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

###代码

**前戏**

是否支持蓝牙

    mBluetoothLe.isSupportBluetooth();

判断蓝牙是否打开

    mBluetoothLe.isBluetoothOpen();

请求打开蓝牙

    mBluetoothLe.enableBluetooth(activity.this);

关闭蓝牙

    mBluetoothLe.disableBluetooth();


**一、获取单例实例**

```java
BluetoothLe mBluetoothLe = BluetoothLe.getDefault();
```

**二、初始化**

	mBluetoothLe.init(this);//必须调用init()初始化

或者使用配置方式进行初始化，这样你可以针对自己的蓝牙产品，做出个性化的蓝牙队列请求。

such as : 发送队列间隔时间设置，因某些公司蓝牙操作要求时间间隔，例如150ms间隔才能发送下一条数据

在Application的onCreate()方法中参照以下方式配置：

```java
    public class BaseApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            BluetoothConfig config = new BluetoothConfig.Builder()
                    .enableQueueInterval(true)//开启队列定时
                    .setQueueIntervalTime(150)//设置定时时长（才会发下一条），单位ms
                    .build();
            BluetoothLe.getDefault().init(this, config);
        }
    }
```

当然，你也可以使用自动的方式来配置蓝牙队列请求。这个时间间隔是通过读取远程蓝牙设备的最小间隔和最大间隔计算得出，保证了队列的最大可用性。

```java
    BluetoothConfig config = new BluetoothConfig.Builder()
            .enableQueueInterval(true)
            .setQueueIntervalTime(BluetoothConfig.AUTO)
            .build();
    BluetoothLe.getDefault().init(this, config);
```

上述的读取远程蓝牙设备的最小间隔和最大间隔，你可以在连上蓝牙发现服务后读取：

```java
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
```

在使用途中修改以上配置：

修改配置：

```java
    BluetoothConfig config = new BluetoothConfig.Builder()
                    .enableQueueInterval(false)
                    .build();
    mBluetoothLe.changeConfig(config);
```

**三、扫描**

扫描过程已携带6.0动态权限申请：地理位置权限

```java
    mBluetoothLe.setScanPeriod(15000)//设置扫描时长，单位毫秒，默认10秒
                .setScanWithDeviceAddress("00:20:ff:34:aa:b3")//根据硬件地址过滤扫描
                .setScanWithServiceUUID("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")//设置根据服务uuid过滤扫描
                .setScanWithDeviceName("ZG1616")//设置根据设备名称过滤扫描
                .setReportDelay(0)//如果为0，则回调onScanResult()方法，如果大于0, 则每隔你设置的时长回调onBatchScanResults()方法，不能小于0
                .startScan(Activity activity);
```

根据多个硬件地址、服务uuid、设备名称过滤扫描，你可以这样：

```java
    .setScanWithDeviceAddress(new String[]{"00:20:ff:34:aa:b3","f3:84:55:b4:ab:7f"})
    .setScanWithServiceUUID(new String[]{"0000180d-0000-1000-8000-00805f9b34fb","6E400001-B5A3-F393-E0A9-E50E24DCCA9E"})
    .setScanWithDeviceName(new String[]{"ZG1616","HaHa"})
```

获取蓝牙扫描状态：

    mBluetoothLe.getScanning();


**停止扫描**

    mBluetoothLe.stopScan();

**四、连接蓝牙、蓝牙连接状态**

```java
	//发送数据、开启通知等操作，必须等待onServicesDiscovered()发现服务回调后，才能去操作
	//参数：false为关闭蓝牙自动重连，如果为true则自动重连
    mBluetoothLe.startConnect(false, mBluetoothDevice);
```

获取蓝牙连接状态：

    mBluetoothLe.getConnected();

获取发现服务状态：

    mBluetoothLe.getServicesDiscovered();


**断开连接**

    mBluetoothLe.disconnect();

**五、发送数据（到蓝牙特征）**

```java
	//以下两个参数为硬件工程师提供，请你与你司的硬件工程师沟通
    private static final String SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    private static final String WRITE_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";

    mBluetoothLe.writeDataToCharacteristic(bytes, SERVICE_UUID, WRITE_UUID);
```


**六、Notification类型通知**

```java
	private static final String SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    private static final String HEART_NOTIFICATION_UUID = "00002a37-0000-1000-8000-00805f9b34fb";
    private static final String STEP_NOTIFICATION_UUID = "0000fff3-0000-1000-8000-00805f9b34fb";
```

开启一个通知

```
	mBluetoothLe.enableNotification(true, SERVICE_UUID, STEP_NOTIFICATION_UUID);
```

开启多个通知

```java
    mBluetoothLe.enableNotification(true, SERVICE_UUID, new String[]{HEART_NOTIFICATION_UUID, STEP_NOTIFICATION_UUID});
```

**七、Indication类型通知**

开启一个通知

```java
	mBluetoothLe.enableIndication(true, SERVICE_UUID, STEP_NOTIFICATION_UUID);
```

开启多个通知

```java
    mBluetoothLe.enableIndication(true, SERVICE_UUID, new String[]{HEART_NOTIFICATION_UUID, STEP_NOTIFICATION_UUID});
```

**八、读取数据**

```java
    private static final String SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    private static final String READ_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";

    mBluetoothLe.readCharacteristic(SERVICE_UUID, READ_UUID);
```

**九、蓝牙信号强度、距离**

```java
    mBluetoothLe.setReadRssiInterval(2000)//设置读取信号强度间隔时间，单位毫秒
            .setOnReadRssiListener(TAG, new OnLeReadRssiListener() {
                @Override
                public void onSuccess(int rssi, int cm) {

                }
            });
```

停止监听蓝牙信号强度

    mBluetoothLe.stopReadRssi();

###监听

```java
//监听扫描
//Every Bluetooth-LE commands status will be callback in here. Flowing listener:
mBleManager.setOnScanListener(TAG, new OnLeScanListener() {
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
```

更多的类似于

```java
mBleManager.setOnConnectListener(...)//监听连接
mBleManager.setOnNotificationListener(...)//监听通知
mBleManager.setOnIndicateListener(...)//监听通知
mBleManager.setOnWriteCharacteristicListener(...)//监听写
mBleManager.setOnReadCharacteristicListener(...)//监听读
mBleManager.setOnReadRssiListener(...)//监听信号强度
```
###其它

**清理蓝牙缓存**

请你在连接上蓝牙后，再执行这步操作

    mBluetoothLe.clearDeviceCache();

**关闭GATT**

在你退出应用的时候使用

    mBluetoothLe.close();

**取消队列**

假设当你在for循环中发送100条数据，想要在中途取消余下的发送

    mBluetoothLe.clearQueue();

###避免内存泄露

在Activity生命周期onDestroy() 中使用：

	mBluetoothLe.destroy();

如果你使用了tag标签的监听，使用: （它的好处是你可以在多个界面产生多个相同的回调）

    mBluetoothLe.destroy(TAG);

取消对应tag：

    mBluetoothLe.cancelTag(TAG);

取消全部tag：

    mBluetoothLe.cancelAllTag();

##仍在补充
1. 一连多台蓝牙设备

##了解更多

1. 强烈建议阅读Demo ： [MainActivity.java](https://github.com/qindachang/BluetoothLELibrary/blob/master/app/src/main/java/com/qindachang/bluetoothlelibrary/MainActivity.java "MainActivity.java") /
[activity_main.xml](https://github.com/qindachang/BluetoothLELibrary/blob/master/app/src/main/res/layout/activity_main.xml "activity_main.xml")

###Thanks

[NordicSemiconductor](https://github.com/NordicSemiconductor/Android-Scanner-Compat-Library "NordicSemiconductor")

1. [philips77](https://github.com/philips77 "philips77")
2. [aldoborrero](https://github.com/aldoborrero "aldoborrero")
