#BluetoothLE library

Low power Bluetooth library. **Advantage**:

1. Adapter to Android5.0 and Android6.0 scan mode (faster).
2. Suitable for xiaomi phone connection Bluetooth operation.
3. Suitable for Samsung mobile phone discovery service, open notification, etc..
4. Support hundreds of method in 'for()' function to write of data, and do not have to worry about it busy.
library use with a queue (like iOS bluetooth queue, you do not have to write delay method).
5. Support at the same time open multiple notification.
6. You can continuous use function such as write/read/notification in for(int i=0;i<100;i++) .

###NOTICE:

1. Android 6.0 phone need location permission when scan bluetooth.
2. write/read/notification operation must be after onServicesDiscovered() being called.
3. Before you connect bluetoothLE device , suggest you close scan.

##Getting Start

###Permission

    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    <uses-permission android:name="android.permission.BLUETOOTH_PRIVILEGED"/>

Android 6.0 above

    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

###Including in your project

	compile 'com.qindachang:BluetoothLELibrary:0.1.1'

###1. Get singleton object

	BluetoothLe mBluetoothLe = BluetoothLe.getDefault();

###2. Initialization

	mBluetoothLe.init(this); //must call this method.

###3. Scan BluetoothLE

    mBluetoothLe.setScanPeriod(15000)//setting scan period, unit millisecond
                .setScanWithServiceUUID("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")//According to service UUID filter scan
                .setScanWithDeviceName("ZG1616")//According to string name filter scan
                .setReportDelay(0)//If it is 0, then the callback onScanResult () method, if more than 0, then every time you set the long callback onBatchScanResults () method, can not be less than 0
                .startBleScan(this, new OnLeScanListener() {
                    @Override
                    public void onScanResult(BluetoothDevice bluetoothDevice, int rssi, ScanRecord scanRecord) {
                        mStringBuilder.append("Scan device：" + bluetoothDevice.getName() + "-rssi：" + rssi + "\n");
                        tv_text.setText(mStringBuilder.toString());
                        mBluetoothDevice = bluetoothDevice;
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        mStringBuilder.append("Batch processing information：" + results.toString() + "\n");
                        tv_text.setText(mStringBuilder.toString());
                    }

                    @Override
                    public void onScanCompleted() {
                        mStringBuilder.append("scan finished! \n");
                        tv_text.setText(mStringBuilder.toString());
                    }

                    @Override
                    public void onScanFailed(int code) {
                        mStringBuilder.append("scan error! \n");
                        tv_text.setText(mStringBuilder.toString());
                    }
                });

###4. Stop scanning

    mBluetoothLe.stopBleScan();

###5. Connect to Bluetooth

'false' means not auto connect.

    mBluetoothLe.startBleConnect(false, mBluetoothDevice, new OnLeConnectListener() {

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
            public void onDeviceConnectFail() {

            }
        });

###6. Disconnect

    mBluetoothLe.disconnect();

###7. Send data to Characteristic

    private static final String SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    private static final String WRITE_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";

    mBluetoothLe.writeDataToCharacteristic(bytes, SERVICE_UUID, WRITE_UUID);

###8. Listening the data sent

    mBluetoothLe.setOnWriteCharacteristicListener(new OnLeWriteCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                Log.d("debug", "has write data:" + Arrays.toString(characteristic.getValue()));

            }

            @Override
            public void onFailed(String msg, int status) {

            }
        });

###9. Enable characteristic notification

	private static final String SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    private static final String HEART_NOTIFICATION_UUID = "00002a37-0000-1000-8000-00805f9b34fb";
    private static final String STEP_NOTIFICATION_UUID = "0000fff3-0000-1000-8000-00805f9b34fb";

	mBluetoothLe.enableBleNotification(true, SERVICE_UUID, STEP_NOTIFICATION_UUID);

###10. Enable many characteristic notification

    mBluetoothLe.enableBleNotification(true, SERVICE_UUID, new String[]{HEART_NOTIFICATION_UUID, STEP_NOTIFICATION_UUID});

###11. Listening characteristic notification

    mBluetoothLe.setBleNotificationListener(new OnLeNotificationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                Log.d("debug", "notification：" + Arrays.toString(characteristic.getValue()));
            }

            @Override
            public void onFailure() {

            }
        });

###12. Read data from characteristic

    private static final String SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    private static final String READ_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";

    mBluetoothLe.readCharacteristic(SERVICE_UUID, READ_UUID);

    mBluetoothLe.setOnReadCharacteristicListener(new OnLeReadCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {

            }

            @Override
            public void onFailure(String info, int status) {

            }
        });

###13. Cancel all queue

    mBluetoothLe.clearQueue();

###14. Close GATT

When you exit app, suggest you call this method.

    mBluetoothLe.close();

###Avoid memory leaks

In Activity's onDestroy() call this method.

	mBluetoothLe.destroy();

##In progress

1. Send data, read the feature, open the notification operation set priority, like the network request as set priority.
2. Bluetooth device signal strength monitoring.
3. Clean up Bluetooth cache.
4. Connection timeout settings, the connection is not on the case of automatic re attached to the number of settings.

##Contact

facebook:
