package com.davistsin.bluetoothlelibrary;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.davistsin.bluetoothle.main.connect.BleConnectCreator;
import com.davistsin.bluetoothle.main.connect.BleConnector;
import com.davistsin.bluetoothle.main.connect.ConnectorSettings;

public class MainViewModel extends AndroidViewModel {
    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void connectBluetooth(BluetoothDevice device) {
        ConnectorSettings connectorSettings = new ConnectorSettings.Builder()
                .autoConnect(true)
                .autoDiscoverServices(true)
                .enableQueue(true)
                .setQueueIntervalTime(ConnectorSettings.QUEUE_INTERVAL_TIME_AUTO)
                .build();
        BleConnector connector = BleConnectCreator.create(getApplication(), device, connectorSettings);
        connector.connect();
    }

}
