package com.davistsin.bluetoothle.main.connect;

public class ConnectorSettings {

    public static int QUEUE_INTERVAL_TIME_AUTO = Integer.MIN_VALUE;

    volatile boolean autoConnect;
    volatile boolean autoDiscoverServices;
    volatile boolean enableQueue;
    volatile int queueIntervalTime;

    private ConnectorSettings(ConnectorSettings.Builder builder) {
        this.autoConnect = builder.autoConnect;
        this.autoDiscoverServices = builder.autoDiscoverServices;
        this.enableQueue = builder.enableQueue;
        this.queueIntervalTime = builder.queueIntervalTime;
    }

    public static class Builder {
        private boolean autoConnect = false;
        private boolean autoDiscoverServices = true;
        private boolean enableQueue = true;
        private int queueIntervalTime = QUEUE_INTERVAL_TIME_AUTO;

        /**
         * 自动重连。
         * @param value
         * @return
         */
        public Builder autoConnect(boolean value) {
            autoConnect = value;
            return this;
        }

        /**
         * 连接成功后，自动发现服务。发现服务后，才能对服务下的特征进行操作。
         * @param value
         * @return
         */
        public Builder autoDiscoverServices(boolean value) {
            autoDiscoverServices = value;
            return this;
        }

        /**
         * 启用队列操作。
         * @param enable
         * @return
         */
        public Builder enableQueue(boolean enable) {
            enableQueue = enable;
            return this;
        }

        /**
         * 队列时间间隔。
         * @param milliseconds ConnectorSettings.QUEUE_INTERVAL_TIME_AUTO 自动读取硬件信息获得间隔时间。或者手动毫秒。
         * @return
         */
        public Builder setQueueIntervalTime(int milliseconds) {
            queueIntervalTime = milliseconds;
            return this;
        }

        public ConnectorSettings build() {
            return new ConnectorSettings(this);
        }
    }
}
