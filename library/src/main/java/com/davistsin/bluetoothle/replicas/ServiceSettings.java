package com.davistsin.bluetoothle.replicas;

import java.util.List;
import java.util.UUID;

/**
 * Created by David Qin on 2017/4/19.
 */

public final class ServiceSettings {
    UUID serviceUuid;
    int serviceType;
    List<ServiceProfile> serviceProfiles;

    private ServiceSettings(ServiceSettings.Builder builder) {
        this.serviceUuid = builder.serviceUuid;
        this.serviceType = builder.serviceType;
        serviceProfiles = builder.serviceProfiles;
    }

    public static final class Builder {
        private UUID serviceUuid;
        private int serviceType;
        private List<ServiceProfile> serviceProfiles;

        public Builder setServiceUuid(UUID serviceUuid) {
            this.serviceUuid = serviceUuid;
            return this;
        }

        public Builder setServiceType(int serviceType) {
            this.serviceType = serviceType;
            return this;
        }

        public Builder addServiceProfiles(List<ServiceProfile> serviceProfiles) {
            this.serviceProfiles = serviceProfiles;
            return this;
        }

        public ServiceSettings build() {
            return new ServiceSettings(this);
        }
    }
}
