package com.qindachang.bluetoothle;

/**
 * Created on 2016/12/13.
 *
 * @author Qin DaChang
 * @see <a href="https://github.com/qindachang">https://github.com/qindachang</a>
 */

public abstract class OnLeRssiListener extends LeListener {
    public abstract void onSuccess(int rssi, int cm);
}
