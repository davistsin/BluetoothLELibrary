package com.davistsin.bluetoothle.main.connect;

import java.util.ArrayList;
import java.util.List;

class BluetoothUtil {

    static int getDistance(int rssi) {
        int irssi = Math.abs(rssi);
        double power = (irssi - 70.0) / (10 * 2.0);
        power = Math.pow(10d, power);
        power = power * 100;
        return (int) power;
    }

    static List<Integer> bytes2IntegerList(byte[] bytes) {
        List<Integer> list = new ArrayList<>();
        for (byte aByte : bytes) {
            int v = aByte & 0xFF;
            list.add(v);
        }
        return list;
    }

}
