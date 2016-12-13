package com.qindachang.bluetoothle;

/**
 * Created on 2016/12/13.
 *
 * @author Qin DaChang
 * @see <a href="https://github.com/qindachang">https://github.com/qindachang</a>
 */

public final class BluetoothConfig {

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

        public Builder setQueueIntervalTime(int queueIntervalTime) {
            this.queueDelayTime = queueIntervalTime;
            this.enableQueueDelay = true;
            return this;
        }

        public Builder enableQueueInterval(boolean enableIntervalDelay) {
            this.enableQueueDelay = enableIntervalDelay;
            return this;
        }

        public BluetoothConfig build() {
            return new BluetoothConfig(this);
        }
    }
}
