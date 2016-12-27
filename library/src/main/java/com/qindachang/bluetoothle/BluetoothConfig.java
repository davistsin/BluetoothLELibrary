package com.qindachang.bluetoothle;

/**
 * Created on 2016/12/13.
 *
 * @author Qin DaChang
 * @see <a href="https://github.com/qindachang">https://github.com/qindachang</a>
 */

public final class BluetoothConfig {

    public static final int AUTO = -1;

    private int queueDelayTime;
    private boolean enableQueueDelay;

    private BluetoothConfig(Builder builder) {
        queueDelayTime = builder.queueDelayTime;
        enableQueueDelay = builder.enableQueueDelay;
    }

    int wtfQueueDelayTime() {
        return queueDelayTime;
    }

    boolean wtfEnableQueueDelay() {
        return enableQueueDelay;
    }

    public static class Builder {
        private int queueDelayTime;
        private boolean enableQueueDelay;

        public Builder setQueueIntervalTime(int millisecond) {
            this.queueDelayTime = millisecond;
            this.enableQueueDelay = true;
            return this;
        }

        public Builder enableQueueInterval(boolean enable) {
            this.enableQueueDelay = enable;
            return this;
        }



        public BluetoothConfig build() {
            return new BluetoothConfig(this);
        }
    }
}
