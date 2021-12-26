package com.davistsin.bluetoothle.replicas;

import android.bluetooth.le.AdvertiseSettings;

/**
 * Created by David Qin on 2017/4/19.
 */

public interface OnAdvertiseListener extends IServerListener {

    /**
     * @param settingsInEffect The actual settings used for advertising, which may be different from
     *                         what has been requested.
     */
    void onStartSuccess(AdvertiseSettings settingsInEffect);

    /**
     * @param errorCode Error code (see ADVERTISE_FAILED_* constants) for advertising start
     *                  failures.
     */
    void onStartFailure(int errorCode);

    void onStopAdvertising();
}
