package com.qindachang.bluetoothle;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;


/* package */ class BleManager {

    private static final String TAG = BleManager.class.getSimpleName();

    private static final UUID SERVICE = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final UUID PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS_UUID = UUID.fromString("00002A04-0000-1000-8000-00805f9b34fb");

    private int REQUEST_PERMISSION_REQ_CODE = 888;

    private boolean isStopScanAfterConnected;
    private boolean isScanning;
    private boolean mConnected;
    private boolean mServiceDiscovered;
    private boolean mRetryConnectEnable;
    private int mRetryConnectCount = 1;
    private int connectTimeoutMillis;
    private int serviceTimeoutMillis;

    private double connIntervalMin;
    private double connIntervalMax;
    private int slaveLatency;
    private int connSupervisionTimeout;
    private int autoQueueInterval = 400;

    private int queueDelayTime;
    private boolean enableQueueDelay;

    private boolean isReadRssi;

    private boolean mAutoConnect;
    private BluetoothDevice mBluetoothDevice;

    private Context mContext;

    private BluetoothGatt mBluetoothGatt;
    private ConnParameters mConnParameters = new ConnParameters();

    private OnLeScanListener mOnLeScanListener;
    private OnLeConnectListener mOnLeConnectListener;
    private OnLeNotificationListener mOnLeNotificationListener;
    private OnLeWriteCharacteristicListener mOnLeWriteCharacteristicListener;
    private OnLeReadCharacteristicListener mOnLeReadCharacteristicListener;

    private RequestQueue mRequestQueue = new RequestQueue();
    private Set<LeListener> mListenerList = new LinkedHashSet<>();

    private int readRssiIntervalMillisecond = 1000;
    private Timer mTimer;
    private TimerTask mTimerTask;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    BleManager(Context context) {
        mContext = context;
    }

    BleManager(Context context, BluetoothConfig config) {
        mContext = context;
        queueDelayTime = config.wtfQueueDelayTime();
        enableQueueDelay = config.wtfEnableQueueDelay();
    }

    void setConfig(BluetoothConfig config) {
        queueDelayTime = config.wtfQueueDelayTime();
        enableQueueDelay = config.wtfEnableQueueDelay();
    }

    boolean isBluetoothOpen() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    boolean enableBluetooth(Activity activity) {
        synchronized (BleManager.class) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Log.e(TAG, "false. your device does not support bluetooth. ");
                return false;
            }
            if (bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "false. your device has been turn on bluetooth.");
                return false;
            }
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivity(intent);
            return true;
        }
    }

    boolean disableBluetooth() {
        synchronized (BleManager.class) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable();
                return true;
            } else {
                Log.d(TAG, "false. your device has been turn off Bluetooth.");
                return false;
            }
        }
    }

    private static boolean isAndroidMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private void runOnUiThread(Runnable runnable) {
        if (isAndroidMainThread()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }

    boolean clearDeviceCache() {
        synchronized (BleManager.class) {
            if (mBluetoothGatt == null) {
                Log.e(TAG, "please connected bluetooth then clear cache.");
                return false;
            }
            try {
                Method e = BluetoothGatt.class.getMethod("refresh", new Class[0]);
                if (e != null) {
                    boolean success = ((Boolean) e.invoke(mBluetoothGatt, new Object[0])).booleanValue();
                    Log.i(TAG, "refresh Device Cache: " + success);
                    return success;
                }
            } catch (Exception exception) {
                Log.e(TAG, "An exception occured while refreshing device", exception);
            }
            return false;
        }
    }

    boolean addLeListenerList(LeListener leListener) {
        return mListenerList.add(leListener);
    }

    void setOnLeScanListener(OnLeScanListener onLeScanListener) {
        mOnLeScanListener = onLeScanListener;
    }

    void scan(Activity activity, List<String> filterDeviceNameList, List<String> filterDeviceAddressList, List<UUID> filerServiceUUIDList,
              int scanPeriod, int reportDelayMillis) {
        Log.d(TAG, "bluetooth le scanning...");
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                return;
            }
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION_REQ_CODE);
            return;
        }

        stopScan();

        BluetoothLeScannerCompat scannerCompat = BluetoothLeScannerCompat.getScanner();
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(reportDelayMillis)
                .setUseHardwareBatchingIfSupported(false)
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        for (String deviceName : filterDeviceNameList) {
            ScanFilter builder = new ScanFilter.Builder().setDeviceName(deviceName).build();
            filters.add(builder);
        }
        for (String deviceAddress : filterDeviceAddressList) {
            ScanFilter builder = new ScanFilter.Builder().setDeviceAddress(deviceAddress).build();
            filters.add(builder);
        }
        for (UUID serviceUUID : filerServiceUUIDList) {
            ScanFilter builder = new ScanFilter.Builder()
                    .setServiceUuid(ParcelUuid.fromString(serviceUUID.toString())).build();
            filters.add(builder);
        }

        scannerCompat.startScan(filters, scanSettings, scanCallback);

        int SCAN_DURATION = scanPeriod;
        if (SCAN_DURATION == 0) {
            SCAN_DURATION = 10000;
        }
        isScanning = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isScanning) {
                    stopScan();
                }
            }
        }, SCAN_DURATION);
    }

    void stopScan() {
        if (isScanning) {
            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(scanCallback);
            isScanning = false;
            for (LeListener leListener : mListenerList) {
                if (leListener instanceof OnLeScanListener) {
                    ((OnLeScanListener) leListener).onScanCompleted();
                }
            }
            if (mOnLeScanListener != null) {
                mOnLeScanListener.onScanCompleted();
            }
            Log.d(TAG, "bluetooth le scan has stop.");
        }
    }


    boolean scanning() {
        return isScanning;
    }

    void setStopScanAfterConnected(boolean set) {
        isStopScanAfterConnected = set;
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult result) {
            for (LeListener leListener : mListenerList) {
                if (leListener instanceof OnLeScanListener) {
                    ((OnLeScanListener) leListener).onScanResult(result.getDevice(), result.getRssi(), result.getScanRecord());
                }
            }
            if (mOnLeScanListener != null) {
                mOnLeScanListener.onScanResult(result.getDevice(), result.getRssi(), result.getScanRecord());
            }
        }

        @Override
        public void onBatchScanResults(final List<ScanResult> results) {
            for (LeListener leListener : mListenerList) {
                if (leListener instanceof OnLeScanListener) {
                    ((OnLeScanListener) leListener).onBatchScanResults(results);
                }
            }
            if (mOnLeScanListener != null) {
                mOnLeScanListener.onBatchScanResults(results);
            }
        }

        @Override
        public void onScanFailed(final int errorCode) {
            for (LeListener leListener : mListenerList) {
                if (leListener instanceof OnLeScanListener) {
                    ((OnLeScanListener) leListener).onScanFailed(errorCode);
                }
            }
            if (mOnLeScanListener != null) {
                mOnLeScanListener.onScanFailed(errorCode);
            }
        }
    };

    void setRetryConnectEnable(boolean retryConnectEnable) {
        mRetryConnectEnable = retryConnectEnable;
    }

    void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    void setServiceTimeoutMillis(int serviceTimeoutMillis) {
        this.serviceTimeoutMillis = serviceTimeoutMillis;
    }

    void setRetryConnectCount(int retryConnectCount) {
        mRetryConnectCount = retryConnectCount;
    }

    boolean connect(boolean autoConnect, final BluetoothDevice device) {
        mAutoConnect = autoConnect;
        mBluetoothDevice = device;
        if (mConnected) {
            Log.d(TAG, "Bluetooth has been connected. connect false.");
            for (LeListener leListener : mListenerList) {
                if (leListener instanceof OnLeConnectListener) {
                    ((OnLeConnectListener) leListener).onDeviceConnectFail();
                }
            }
            if (mOnLeConnectListener != null) {
                mOnLeConnectListener.onDeviceConnectFail();
            }
            return false;
        }
        if (mBluetoothGatt != null) {
            Log.d(TAG, "The BluetoothGatt already exist, set it close() and null.");
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            mConnected = false;
        }
        Log.d(TAG, "create new device connection for BluetoothGatt. ");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = device.connectGatt(mContext, autoConnect, mGattCallback, TRANSPORT_LE);
        } else {
            mBluetoothGatt = device.connectGatt(mContext, autoConnect, mGattCallback);
        }
        for (LeListener leListener : mListenerList) {
            if (leListener instanceof OnLeConnectListener) {
                ((OnLeConnectListener) leListener).onDeviceConnecting();
            }
        }
        if (mOnLeConnectListener != null) {
            mOnLeConnectListener.onDeviceConnecting();
        }

        checkConnected();

        return true;
    }

    BluetoothDevice getBluetoothDevice() {
        if (mConnected) {
            return mBluetoothDevice;
        } else
            return null;
    }

    BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }

    private void checkConnected() {
        if (mRetryConnectEnable && mRetryConnectCount > 0 && connectTimeoutMillis > 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean connected = getConnected();
                    if (!connected) {
                        connect(mAutoConnect, mBluetoothDevice);
                        mRetryConnectCount = mRetryConnectCount - 1;
                    }
                }
            }, connectTimeoutMillis);
        }
    }

    boolean getConnected() {
        return mConnected;
    }

    boolean getServicesDiscovered() {
        return mServiceDiscovered;
    }

    void setConnectListener(OnLeConnectListener onLeConnectListener) {
        mOnLeConnectListener = onLeConnectListener;
    }

    private boolean enableNotification(boolean enable, BluetoothGattCharacteristic characteristic) {
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null || characteristic == null)
            return false;
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
            Log.e(TAG, "uuid:" + characteristic.getUuid() + ", does not support notification");
            return false;
        }
        gatt.setCharacteristicNotification(characteristic, enable);
        Log.d(TAG, "setCharacteristicNotification uuid:" + characteristic.getUuid() + " ," + enable);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            Log.d(TAG, "writeDescriptor(notification), " + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
            return gatt.writeDescriptor(descriptor);
        }
        return false;
    }

    void enableNotificationQueue(boolean enable, UUID serviceUUID, UUID[] characteristicUUIDs) {
        BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
        if (service != null) {
            for (UUID characteristicUUID : characteristicUUIDs) {
                mRequestQueue.addRequest(Request.newEnableNotificationsRequest(enable, service.getCharacteristic(characteristicUUID)));
            }
        }
    }

    void setOnLeNotificationListener(OnLeNotificationListener onLeNotificationListener) {
        this.mOnLeNotificationListener = onLeNotificationListener;
    }

    private boolean enableIndication(boolean enable, BluetoothGattCharacteristic characteristic) {
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null || characteristic == null) {
            return false;
        }
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) == 0) {
            Log.e(TAG, "uuid:" + characteristic.getUuid() + ", does not support indication");
            return false;
        }
        gatt.setCharacteristicNotification(characteristic, enable);
        Log.d(TAG, "setCharacteristicNotification uuid:" + characteristic.getUuid() + " ," + enable);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            Log.d(TAG, "writeDescriptor(indication), " + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
            return gatt.writeDescriptor(descriptor);
        }
        return false;
    }

    void enableIndicationQueue(boolean enable, UUID serviceUUID, UUID[] characteristicUUIDs) {
        BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
        if (service != null) {
            for (UUID characteristicUUID : characteristicUUIDs) {
                mRequestQueue.addRequest(Request.newEnableIndicationsRequest(enable, service.getCharacteristic(characteristicUUID)));
            }
        }
    }

    void writeCharacteristicQueue(byte[] bytes, UUID serviceUUID, UUID characteristicUUID) {
        if (mBluetoothGatt == null || serviceUUID == null || characteristicUUID == null) {
            Log.d(TAG, "the bluetooth gatt or serviceUUID or characteristicUUID is null. ");
            return;
        }
        BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            mRequestQueue.addRequest(Request.newWriteRequest(characteristic, bytes));
        }
    }

    private boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null || characteristic == null)
            return false;
        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) == 0)
            return false;
        return gatt.writeCharacteristic(characteristic);
    }

    void setWriteCharacteristicListener(OnLeWriteCharacteristicListener onLeWriteCharacteristicListener) {
        mOnLeWriteCharacteristicListener = onLeWriteCharacteristicListener;
    }

    void readCharacteristicQueue(UUID serviceUUID, UUID characteristicUUID) {
        BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            mRequestQueue.addRequest(Request.newReadRequest(characteristic));
        }
    }

    private boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null || characteristic == null)
            return false;
        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) == 0)
            return false;
        return gatt.readCharacteristic(characteristic);
    }

    void setOnLeReadCharacteristicListener(OnLeReadCharacteristicListener onLeReadCharacteristicListener) {
        mOnLeReadCharacteristicListener = onLeReadCharacteristicListener;
    }

    void readRssi() {
        if (mConnected) {
            readRssiTimerTask();
        } else {
            isReadRssi = true;
        }
    }

    void setReadRssiIntervalMillisecond(int millisecond) {
        readRssiIntervalMillisecond = millisecond;
    }

    void cancelReadRssiTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void readRssiTimerTask() {
        mTimer = null;
        mTimerTask = null;
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.readRemoteRssi();
                }
            }
        };
        mTimer.schedule(mTimerTask, 100, readRssiIntervalMillisecond);
    }

    void disconnect() {
        if (mConnected && mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mConnected = false;
            mServiceDiscovered = false;
        }
    }

    void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            mConnected = false;
            mServiceDiscovered = false;
        }
    }

    private void checkServiceDiscover() {
        if (mRetryConnectEnable && mRetryConnectCount > 0 && serviceTimeoutMillis > 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!mServiceDiscovered) {
                        connect(mAutoConnect, mBluetoothDevice);
                        mRetryConnectCount -= 1;
                    }
                }
            }, serviceTimeoutMillis);
        }
    }

    private void readConnectionParameters() {
        if (mBluetoothGatt == null) {
            return;
        }
        readCharacteristicQueue(SERVICE, PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS_UUID);
    }

    ConnParameters getConnParameters() {
        return mConnParameters;
    }

    private BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "device connect success!");
                mConnected = true;
                if (isStopScanAfterConnected) {
                    stopScan();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (LeListener leListener : mListenerList) {
                            if (leListener instanceof OnLeConnectListener) {
                                ((OnLeConnectListener) leListener).onDeviceConnected();
                            }
                        }
                        if (mOnLeConnectListener != null) {
                            mOnLeConnectListener.onDeviceConnected();
                        }
                    }
                });

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_BONDING) {
                            if (mBluetoothGatt != null) {
                                mBluetoothGatt.discoverServices();
                                checkServiceDiscover();
                            }
                        }
                    }
                }, 600);

                if (isReadRssi) {
                    cancelReadRssiTimerTask();
                    readRssiTimerTask();
                }

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "device disconnect.");
                mConnected = false;
                mServiceDiscovered = false;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (LeListener leListener : mListenerList) {
                            if (leListener instanceof OnLeConnectListener) {
                                ((OnLeConnectListener) leListener).onDeviceDisconnected();
                            }
                        }
                        if (mOnLeConnectListener != null) {
                            mOnLeConnectListener.onDeviceDisconnected();
                        }
                    }
                });
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "success with find services discovered .");
                mServiceDiscovered = true;

                readConnectionParameters();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (LeListener leListener : mListenerList) {
                            if (leListener instanceof OnLeConnectListener) {
                                ((OnLeConnectListener) leListener).onServicesDiscovered(gatt);
                            }
                        }
                        if (mOnLeConnectListener != null) {
                            mOnLeConnectListener.onServicesDiscovered(gatt);
                        }
                    }
                });

            } else {
                Log.d(TAG, "failure find services discovered.");
                mServiceDiscovered = false;
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            //read
            if (status == BluetoothGatt.GATT_SUCCESS) {

                if (characteristic.getUuid().equals(PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS_UUID)) {
                    List<Integer> parameters = Utils.bytes2IntegerList(characteristic.getValue());
                    connIntervalMin = (parameters.get(1) * 16 + parameters.get(0)) * 1.25;
                    connIntervalMax = (parameters.get(3) * 16 + parameters.get(2)) * 1.25;
                    slaveLatency = parameters.get(5) * 16 + parameters.get(4);
                    connSupervisionTimeout = parameters.get(7) * 16 + parameters.get(6);
                    mConnParameters.setUUID(PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS_UUID);
                    mConnParameters.setConnIntervalMin(connIntervalMin);
                    mConnParameters.setConnIntervalMax(connIntervalMax);
                    mConnParameters.setProperties("READ");
                    mConnParameters.setSlaveLatency(slaveLatency);
                    mConnParameters.setSupervisionTimeout(connSupervisionTimeout);
                    autoQueueInterval = (int) connIntervalMax + 50;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (LeListener leListener : mListenerList) {
                                if (leListener instanceof OnLeReadCharacteristicListener) {
                                    ((OnLeReadCharacteristicListener) leListener).onSuccess(characteristic);
                                }
                            }
                            if (mOnLeReadCharacteristicListener != null) {
                                mOnLeReadCharacteristicListener.onSuccess(characteristic);
                            }
                        }
                    });
                }

            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (LeListener leListener : mListenerList) {
                            if (leListener instanceof OnLeReadCharacteristicListener) {
                                ((OnLeReadCharacteristicListener) leListener).onFailure("Phone has lost bonding information", status);
                            }
                        }
                        if (mOnLeReadCharacteristicListener != null) {
                            mOnLeReadCharacteristicListener.onFailure("Phone has lost bonding information", status);
                        }
                    }
                });

            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (LeListener leListener : mListenerList) {
                            if (leListener instanceof OnLeReadCharacteristicListener) {
                                ((OnLeReadCharacteristicListener) leListener).onFailure("Error on reading characteristic", status);
                            }
                        }
                        if (mOnLeReadCharacteristicListener != null) {
                            mOnLeReadCharacteristicListener.onFailure("Error on reading characteristic", status);
                        }
                    }
                });

            }

            mRequestQueue.next();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (LeListener leListener : mListenerList) {
                            if (leListener instanceof OnLeWriteCharacteristicListener) {
                                ((OnLeWriteCharacteristicListener) leListener).onSuccess(characteristic);
                            }
                        }
                        if (mOnLeWriteCharacteristicListener != null) {
                            mOnLeWriteCharacteristicListener.onSuccess(characteristic);
                        }
                    }
                });

            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (LeListener leListener : mListenerList) {
                            if (leListener instanceof OnLeWriteCharacteristicListener) {
                                ((OnLeWriteCharacteristicListener) leListener).onFailed("Phone has lost of bonding information. ", status);
                            }
                        }
                        if (mOnLeWriteCharacteristicListener != null) {
                            mOnLeWriteCharacteristicListener.onFailed("Phone has lost of bonding information. ", status);
                        }
                    }
                });

            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (LeListener leListener : mListenerList) {
                            if (leListener instanceof OnLeWriteCharacteristicListener) {
                                ((OnLeWriteCharacteristicListener) leListener).onFailed("Error on reading characteristic.", status);
                            }
                        }
                        if (mOnLeWriteCharacteristicListener != null) {
                            mOnLeWriteCharacteristicListener.onFailed("Error on reading characteristic. ", status);
                        }
                    }
                });

            }
            mRequestQueue.next();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final BluetoothGattDescriptor cccd = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
                    final boolean notifications = cccd == null || cccd.getValue() == null || cccd.getValue().length != 2 || cccd.getValue()[0] == 0x01;

                    if (notifications) {
                        for (LeListener leListener : mListenerList) {
                            if (leListener instanceof OnLeNotificationListener) {
                                ((OnLeNotificationListener) leListener).onSuccess(characteristic);
                            }
                        }
                        if (mOnLeNotificationListener != null) {
                            mOnLeNotificationListener.onSuccess(characteristic);
                        }
                    } else {
                        for (LeListener leListener : mListenerList) {
                            if (leListener instanceof OnLeIndicationListener) {
                                ((OnLeIndicationListener) leListener).onSuccess(characteristic);
                            }
                        }
                    }
                }
            });

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            mRequestQueue.next();
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, final int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (LeListener leListener : mListenerList) {
                            if (leListener instanceof OnLeReadRssiListener) {
                                ((OnLeReadRssiListener) leListener).onSuccess(rssi, Utils.getDistance(rssi));
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    void destroy() {
        synchronized (BleManager.class) {
            mOnLeScanListener = null;
            mOnLeConnectListener = null;
            mOnLeNotificationListener = null;
            mOnLeWriteCharacteristicListener = null;
            mOnLeReadCharacteristicListener = null;
        }
    }

    void destroy(Object tag) {
        cancelTag(tag);
    }

    void cancelTag(Object tag) {
        synchronized (BleManager.class) {
            List<LeListener> leListenerList = new ArrayList<>();
            for (LeListener leListener : mListenerList) {
                if (leListener.getTag() == tag) {
                    leListenerList.add(leListener);
                }
            }
            cancelTagList(leListenerList);
        }
    }

    private void cancelTagList(List<LeListener> list) {
        if (list.size() > 0 && !list.isEmpty()) {
            mListenerList.remove(list.get(0));
            if (list.size() > 0 && !list.isEmpty()) {
                list.remove(0);
                cancelTagList(list);
            }
        }
    }

    void cancelAllTag() {
        synchronized (BleManager.class) {
            mListenerList.clear();
        }
    }

    void clearQueue() {
        synchronized (BleManager.class) {
            mRequestQueue.cancelAll();
        }
    }

    private class RequestQueue {

        private Queue<Request> mRequestQueue = new LinkedList<>();

        void addRequest(Request request) {
            int oldSize = mRequestQueue.size();
            mRequestQueue.add(request);
            if (mRequestQueue.size() == 1 && oldSize == 0) {
                startExecutor();
            }
        }

        private void startExecutor() {
            Request request = mRequestQueue.peek();
            switch (request.type) {
                case WRITE:
                    BluetoothGattCharacteristic characteristic = request.getCharacteristic();
                    if (characteristic != null) {
                        characteristic.setValue(request.getBytes());
                    }
                    writeCharacteristic(characteristic);
                    break;
                case READ:
                    readCharacteristic(request.getCharacteristic());
                    break;
                case ENABLE_NOTIFICATIONS:
                    enableNotification(request.isEnable(), request.getCharacteristic());
                    break;
                case ENABLE_INDICATIONS:
                    enableIndication(request.isEnable(), request.getCharacteristic());
                    break;
            }
        }

        void next() {
            if (enableQueueDelay) {
                if (queueDelayTime < 0) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runQueue();
                        }
                    }, autoQueueInterval);
                } else {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runQueue();
                        }
                    }, queueDelayTime);
                }
            } else {
                runQueue();
            }
        }

        void runQueue() {
            mRequestQueue.poll();
            if (mRequestQueue != null && mRequestQueue.size() > 0) {
                startExecutor();
            }
        }

        void cancelAll() {
            mRequestQueue.clear();
        }

    }

}
