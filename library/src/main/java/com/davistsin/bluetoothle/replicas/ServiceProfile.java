package com.davistsin.bluetoothle.replicas;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by David Qin on 2017/4/19.
 */

public class ServiceProfile implements Parcelable {
    private UUID characteristicUuid;
    private int characteristicProperties;
    private int characteristicPermission;

    private UUID descriptorUuid;
    private int descriptorPermission;
    private byte[] descriptorValue;

    public UUID getCharacteristicUuid() {
        return characteristicUuid;
    }

    public void setCharacteristicUuid(UUID characteristicUuid) {
        this.characteristicUuid = characteristicUuid;
    }

    public int getCharacteristicProperties() {
        return characteristicProperties;
    }

    public void setCharacteristicProperties(int characteristicProperties) {
        this.characteristicProperties = characteristicProperties;
    }

    public int getCharacteristicPermission() {
        return characteristicPermission;
    }

    public void setCharacteristicPermission(int characteristicPermission) {
        this.characteristicPermission = characteristicPermission;
    }

    public UUID getDescriptorUuid() {
        return descriptorUuid;
    }

    public void setDescriptorUuid(UUID descriptorUuid) {
        this.descriptorUuid = descriptorUuid;
    }

    public int getDescriptorPermission() {
        return descriptorPermission;
    }

    public void setDescriptorPermission(int descriptorPermission) {
        this.descriptorPermission = descriptorPermission;
    }

    public byte[] getDescriptorValue() {
        return descriptorValue;
    }

    public void setDescriptorValue(byte[] descriptorValue) {
        this.descriptorValue = descriptorValue;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.characteristicUuid);
        dest.writeInt(this.characteristicProperties);
        dest.writeInt(this.characteristicPermission);
        dest.writeSerializable(this.descriptorUuid);
        dest.writeInt(this.descriptorPermission);
        dest.writeByteArray(this.descriptorValue);
    }

    public ServiceProfile() {
    }

    protected ServiceProfile(Parcel in) {
        this.characteristicUuid = (UUID) in.readSerializable();
        this.characteristicProperties = in.readInt();
        this.characteristicPermission = in.readInt();
        this.descriptorUuid = (UUID) in.readSerializable();
        this.descriptorPermission = in.readInt();
        this.descriptorValue = in.createByteArray();
    }

    public static final Creator<ServiceProfile> CREATOR = new Creator<ServiceProfile>() {
        @Override
        public ServiceProfile createFromParcel(Parcel source) {
            return new ServiceProfile(source);
        }

        @Override
        public ServiceProfile[] newArray(int size) {
            return new ServiceProfile[size];
        }
    };

    @Override
    public String toString() {
        return "ServiceProfile{" +
                "characteristicUuid=" + characteristicUuid +
                ", characteristicProperties=" + characteristicProperties +
                ", characteristicPermission=" + characteristicPermission +
                ", descriptorUuid=" + descriptorUuid +
                ", descriptorPermission=" + descriptorPermission +
                ", descriptorValue=" + Arrays.toString(descriptorValue) +
                '}';
    }
}
