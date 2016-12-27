package com.qindachang.bluetoothlelibrary;

import android.app.Application;

import com.qindachang.bluetoothle.BluetoothConfig;
import com.qindachang.bluetoothle.BluetoothLe;

/**
 * Created on 2016/12/13.
 *
 * @author Qin DaChang
 * @see <a href="https://github.com/qindachang">https://github.com/qindachang</a>
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothConfig config = new BluetoothConfig.Builder()
                .enableQueueInterval(true)
                .setQueueIntervalTime(BluetoothConfig.AUTO)
                .build();
        BluetoothLe.getDefault().init(this, config);
    }
}
