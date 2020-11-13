package com.ch.devices.store;

import com.ch.devices.model.Device;

public interface DeviceStorage {
    Device getDevice(String serialNumber, String machineCode);
    DeviceStoreResponse createDevice(Device device);
    DeviceStoreResponse updateDevice(Device device);
}
