package com.qindachang.bluetoothlelibrary;

import java.util.UUID;

/**
 * 使用时替换下列为你对应的uuid
 * Created by qin on 2017/1/31.
 */

public interface BluetoothUUID {
    UUID SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    UUID READ = UUID.fromString("0000fff5-0000-1000-8000-00805f9b34fb");
    UUID WRITE = UUID.fromString("0000fff5-0000-1000-8000-00805f9b34fb");
    UUID STEP_NOTIFICATION = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb");
    UUID HR_NOTIFICATION = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    //电池电量
    UUID BATTERY_SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    UUID BATTERY_NOTIFICATION = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
}
