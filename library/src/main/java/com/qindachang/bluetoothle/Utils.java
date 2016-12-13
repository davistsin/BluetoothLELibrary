package com.qindachang.bluetoothle;

/**
 * Created on 2016/12/13.
 *
 * @author Qin DaChang
 * @see <a href="https://github.com/qindachang">https://github.com/qindachang</a>
 */

public class Utils {

    public static int getDistance(int rssi) {
        int irssi = Math.abs(rssi);
        double power = (irssi - 70.0) / (10 * 2.0);
        power = Math.pow(10d, power);
        power = power * 100;
        return (int) power;
    }

}
