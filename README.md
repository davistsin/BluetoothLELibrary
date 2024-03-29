![title](./image/title.jpg)

[![](https://jitpack.io/v/davistsin/BluetoothLELibrary.svg)](https://jitpack.io/#davistsin/BluetoothLELibrary)  ![license](https://img.shields.io/github/license/davistsin/BluetoothLELibrary)

[固件升级/硬件升级/DFU](https://github.com/qindachang/DFUDemo "固件升级/硬件升级/DFU")

低功耗蓝牙库。**优势**：

1. **具有**主机、从机模式。在从机模式中，本机也可以开启蓝牙服务、读写特征。
2. 同时连接多台蓝牙设备。
3. 适配小米手机连接蓝牙操作。
4. 适配三星手机发现服务、开启通知等。
5. 支持**直接连发数百条**数据，而不用担心消息发不出。自带消息队列（终于可以像iOS一样啦，不用去写延时啦）。
6. 支持同时**开启多个通知**。
7. 可以连续操作发送数据、读取特征、开启通知，即使你在for循环中写也没问题，**自带队列**。
8. 队列定时设置，满足因公司需求蓝牙时间间隔。
9. 设备信号强度、距离计算回调，可用于防丢器产品。

### 注意点：

1. Android 6.0 扫描蓝牙需要地理位置权限。[easypermissions](https://github.com/googlesamples/easypermissions "easypermissions")
2. Android 7.0 扫描蓝牙需要地理位置权限，并且需要开启系统位置信息。
[LocationUtils](https://github.com/qindachang/BluetoothLELibrary/blob/master/app/src/main/java/com/qindachang/bluetoothlelibrary/LocationUtils.java "LocationUtils")
3. Android 12 需要声明 BLUETOOTH_SCAN、BLUETOOTH_CONNECT 权限；如果使用蓝牙从机，则需要 BLUETOOTH_ADVERTISE 权限。（targets API 大于等于31时）
4. 发送数据、开启通知、读取特征等操作，需要在onServicesDiscovered()发现服务之后才能进行。
5. 连接设备之前最好先停止扫描（小米手机可能会出现不能发现服务的情况）。

## 开始

### 集成

添加依赖

```

allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.davistsin:BluetoothLELibrary:{latest version}'
}

```

蓝牙扫描推荐使用 [Android-Scanner-Compat-Library](https://github.com/NordicSemiconductor/Android-Scanner-Compat-Library)

```
implementation 'no.nordicsemi.android.support.v18:scanner:1.6.0'
```

**权限：**

```
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
```

5.0以上需要

```
<!-- 只有当你的 targets API 等于或大于 Android 5.0 (API level 21) 才需要此权限 -->
<uses-feature android:name="android.hardware.location.gps" />
```

6.0以上需要

```
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```

12以上需要

```
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />

<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
```

---

### 一、 使用介绍（主机模式）

是否支持蓝牙  

```Java
BleHelper.isSupportBluetooth();
```

蓝牙是否打开

```Java
BleHelper.isBluetoothEnable();
```

打开蓝牙

```Java
BleHelper.enableBluetooth();
```

关闭蓝牙

```Java
BleHelper.disableBluetooth();
```

获取已连接的蓝牙设备列表

```Java
BleHelper.getConnectedDevices(this);
```

#### 1. 扫描

文档前往：

[https://github.com/NordicSemiconductor/Android-Scanner-Compat-Library](https://github.com/NordicSemiconductor/Android-Scanner-Compat-Library)

#### 2. 连接

可以创建多个连接实例

```java
ConnectorSettings settings = new ConnectorSettings.Builder()
        .autoConnect(true)
        .autoDiscoverServices(true)
        .enableQueue(true)
        .setQueueIntervalTime(ConnectorSettings.QUEUE_INTERVAL_TIME_AUTO)
        .build();
BleConnector bleConnector = BleConnectCreator.create(MainActivity.this, bluetoothDevice, settings);

bleConnector.connect();

bleConnector.addConnectionListener(new BleConnectionListener() {
    @Override
    public void onConnecting() {

    }

    @Override
    public void onConnected(BluetoothDevice device) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onFailure() {

    }
});
```
#### 3. 发现服务

```java
bleConnector.discoverServices();

bleConnector.addDiscoveryListener(new BleDiscoverServicesListener() {
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt) {

    }

    @Override
    public void onFailure() {

    }
});
```
#### 4. 读取数据

```java
bleConnector.readCharacteristic("serviceUUID", "characteristicUUID");

bleConnector.addReadCharacteristicListener(new BleReadCharacteristicListener() {
    @Override
    public void onSuccess(BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
    }

    @Override
    public void onFailure() {

    }
});
```
#### 5. 发送数据
```java
bleConnector.writeCharacteristic(new byte[]{0x01,0x02,0x03}, "serviceUUID", "characteristicUUID");

bleConnector.addWriteCharacteristicListener(new BleWriteCharacteristicListener() {
    @Override
    public void onSuccess(BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onFailed() {

    }
});
```
#### 6. 通知
```java
// 打开Notification
bleConnector.enableNotification(true, "serviceUUID", "characteristicUUID");
// 关闭Notification
bleConnector.enableNotification(false, "serviceUUID", "characteristicUUID");

bleConnector.addNotificationListener(new BleNotificationListener() {
    @Override
    public void onSuccess(BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
    }

    @Override
    public void onFailed() {

    }
});

// 打开Indication
bleConnector.enableIndication(true, "serviceUUID", "characteristicUUID");
// 关闭Indication
bleConnector.enableIndication(false, "serviceUUID", "characteristicUUID");

bleConnector.addIndicationListener(new BleIndicationListener() {
    @Override
    public void onSuccess(BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
    }

    @Override
    public void onFailed() {

    }
});
```
#### 7. 移除监听、关闭连接
```java
bleConnector.removeAllListeners();

bleConnector.close();
```

---

### 二、使用介绍（从机模式）

#### 1. 启动

```java
private BleGattServer mGattServer = new BleGattServer();

mGattServer.startAdvertising(UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")); // 该uuid可提供给主机client过滤扫描，可以自定义
mGattServer.startServer(context);
```
#### 2. 开始广播/停止广播

```java
mGattServer.startAdvertising("serviceUUID");

mGattServer.stopAdvertising();
```

#### 4. 添加蓝牙服务Service

```java
List<ServiceProfile> list = new ArrayList<>();

// 设置一个写的特征
ServiceProfile profile = new ServiceProfile();
profile.setCharacteristicUuid(UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb"));
profile.setCharacteristicProperties(BluetoothGattCharacteristic.PROPERTY_WRITE);
profile.setCharacteristicPermission(BluetoothGattCharacteristic.PERMISSION_WRITE);
profile.setDescriptorUuid(GattServer.CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
profile.setDescriptorPermission(BluetoothGattDescriptor.PERMISSION_READ);
profile.setDescriptorValue(new byte[]{0});
list.add(profile);

// 设置一个读的特征
ServiceProfile profile1 = new ServiceProfile();
profile1.setCharacteristicUuid(UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb"));
profile1.setCharacteristicProperties(BluetoothGattCharacteristic.PROPERTY_READ);
profile1.setCharacteristicPermission(BluetoothGattCharacteristic.PERMISSION_READ);
profile1.setDescriptorUuid(GattServer.CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
profile1.setDescriptorPermission(BluetoothGattDescriptor.PERMISSION_READ);
profile1.setDescriptorValue(new byte[]{1});
list.add(profile1);

// 设置一个notify通知
ServiceProfile profile2 = new ServiceProfile();
profile2.setCharacteristicUuid(UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"));
profile2.setCharacteristicProperties(BluetoothGattCharacteristic.PROPERTY_NOTIFY);
profile2.setCharacteristicPermission(BluetoothGattCharacteristic.PERMISSION_READ);
profile2.setDescriptorUuid(GattServer.CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
profile2.setDescriptorPermission(BluetoothGattDescriptor.PERMISSION_WRITE);
profile2.setDescriptorValue(new byte[]{1});
list.add(profile2);

final ServiceSettings serviceSettings = new ServiceSettings.Builder()
        .setServiceUuid(UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"))//服务uuid
        .setServiceType(BluetoothGattService.SERVICE_TYPE_PRIMARY)
        .addServiceProfiles(list)//上述设置添加到该服务里
        .build();

mGattServer.addService(serviceSettings);
```

#### 4. 回调监听

```java
mGattServer.addOnAdvertiseListener(new OnAdvertiseListener() {
    @Override
    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
        setContentText("开启广播  成功，uuid：0000fff0-0000-1000-8000-00805f9b34fb");
    }

    @Override
    public void onStartFailure(int errorCode) {
        setContentText("开启广播  失败，uuid：0000fff0-0000-1000-8000-00805f9b34fb");
    }

    @Override
    public void onStopAdvertising() {
        setContentText("停止广播，uuid：0000fff0-0000-1000-8000-00805f9b34fb");
    }
});

mGattServer.addOnServiceAddedListener(new OnServiceAddedListener() {
    @Override
    public void onSuccess(BluetoothGattService service) {
        setContentText("添加服务成功！");
    }

    @Override
    public void onFail(BluetoothGattService service) {
        setContentText("添加服务失败");
    }
});

mGattServer.addOnConnectionStateChangeListener(new OnConnectionStateChangeListener() {
    @Override
    public void onChange(BluetoothDevice device, int status, int newState) {

    }

    @Override
    public void onConnected(BluetoothDevice device) {
        setContentText("连接上一台设备 ：{ name = " + device.getName() + ", address = " + device.getAddress() + "}");
        mBluetoothDevice = device;
    }

    @Override
    public void onDisconnected(BluetoothDevice device) {
        setContentText("设备断开连接 ：{ name = " + device.getName() + ", address = " + device.getAddress() + "}");
    }
});

mGattServer.addOnWriteRequestListener(new OnWriteRequestListener() {
    @Override
    public void onCharacteristicWritten(BluetoothDevice device, BluetoothGattCharacteristic characteristic, byte[] value) {
        setContentText("设备写入特征请求 ： device = " + device.getAddress() + ", characteristic uuid = " + characteristic.getUuid().toString() + ", value = " + Arrays.toString(value));
    }

    @Override
    public void onDescriptorWritten(BluetoothDevice device, BluetoothGattDescriptor descriptor, byte[] value) {
        setContentText("设备写入描述请求 ： device = " + device.getAddress() + ", descriptor uuid = " + descriptor.getUuid().toString() + ", value = " + Arrays.toString(value));
    }
});

```

#### 5. 移除监听、关闭

```java
mGattServer.removeAllListeners();

mGattServer.closeServer();
```
