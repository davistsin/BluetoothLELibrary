/*
 * Copyright (c) 2016, Qin Dachang
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qindachang.bluetoothle;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by admin on 2016/12/28.
 */

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

    public static final Parcelable.Creator<ConnParameters> CREATOR = new Parcelable.Creator<ConnParameters>() {
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
