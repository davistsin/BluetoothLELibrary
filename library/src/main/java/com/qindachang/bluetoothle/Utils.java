package com.qindachang.bluetoothle;

import android.content.Context;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/12/13.
 *
 * @author Qin DaChang
 * @see <a href="https://github.com/qindachang">https://github.com/qindachang</a>
 */

/* package */ class Utils {

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

    static boolean isOpenLocationService(Context context) {
        boolean isGps = false;
        boolean isNetwork = false;

        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                isGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetwork   = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
            if (isGps || isNetwork) {
                return true;
            }
        }
        return false;
    }

}
