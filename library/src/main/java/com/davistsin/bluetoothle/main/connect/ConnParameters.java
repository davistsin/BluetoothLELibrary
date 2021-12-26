package com.davistsin.bluetoothle.main.connect;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class ConnParameters implements Parcelable {

    private UUID mUUID = UUID.fromString("00002A04-0000-1000-8000-00805f9b34fb");
    private String mProperties = "";
    private double connIntervalMin;
    private double connIntervalMax;
    private int slaveLatency;
    private int supervisionTimeout;

    public UUID getUUID() {
        return mUUID;
    }

    public void setUUID(UUID UUID) {
        mUUID = UUID;
    }

    public String getProperties() {
        return mProperties;
    }

    public void setProperties(String properties) {
        mProperties = properties;
    }

    public double getConnIntervalMin() {
        return connIntervalMin;
    }

    public void setConnIntervalMin(double connIntervalMin) {
        this.connIntervalMin = connIntervalMin;
    }

    public double getConnIntervalMax() {
        return connIntervalMax;
    }

    public void setConnIntervalMax(double connIntervalMax) {
        this.connIntervalMax = connIntervalMax;
    }

    public int getSlaveLatency() {
        return slaveLatency;
    }

    public void setSlaveLatency(int slaveLatency) {
        this.slaveLatency = slaveLatency;
    }

    public int getSupervisionTimeout() {
        return supervisionTimeout;
    }

    public void setSupervisionTimeout(int supervisionTimeout) {
        this.supervisionTimeout = supervisionTimeout;
    }

    @Override
    public String toString() {
        return "ConnParameters:{UUID = " + mUUID.toString() + ", Connection Interval Min = " + connIntervalMin
                + ", Connection Interval Max = " + connIntervalMax
                + ", Slave Latency = " + slaveLatency + ", Supervision Timeout Multiplier = "
                + supervisionTimeout + "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.mUUID);
        dest.writeString(this.mProperties);
        dest.writeDouble(this.connIntervalMin);
        dest.writeDouble(this.connIntervalMax);
        dest.writeInt(this.slaveLatency);
        dest.writeInt(this.supervisionTimeout);
    }

    public ConnParameters() {
    }

    protected ConnParameters(Parcel in) {
        this.mUUID = (UUID) in.readSerializable();
        this.mProperties = in.readString();
        this.connIntervalMin = in.readDouble();
        this.connIntervalMax = in.readDouble();
        this.slaveLatency = in.readInt();
        this.supervisionTimeout = in.readInt();
    }

    public static final Creator<ConnParameters> CREATOR = new Creator<ConnParameters>() {
        @Override
        public ConnParameters createFromParcel(Parcel source) {
            return new ConnParameters(source);
        }

        @Override
        public ConnParameters[] newArray(int size) {
            return new ConnParameters[size];
        }
    };
}
