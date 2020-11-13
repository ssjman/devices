package com.ch.devices.store;

import com.ch.devices.model.Device;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Data
public class DeviceStore implements DeviceStorage {
    private List<Device> devices = new ArrayList<>();

    @Override
    public Device getDevice(String serialNumber, String machineCode) {
        return devices.stream()
                .filter(device -> device.getSerialNumber().equals(serialNumber) &&
                        device.getMachineCode().equals(machineCode)).findFirst().orElse(null);
    }

    @Override
    public DeviceStoreResponse createDevice(Device device) {
        DeviceStoreResponse deviceStoreResponse = new DeviceStoreResponse();
        if (!deviceExists(device)) {
            devices.add(device);
            deviceStoreResponse.setCreated(true);
        } else {
            deviceStoreResponse.setCreated(false);
        }
        return deviceStoreResponse;
    }

    @Override
    public DeviceStoreResponse updateDevice(Device device) {
        DeviceStoreResponse deviceStoreResponse = new DeviceStoreResponse();
        if (deviceExists(device)) {
            List<Device> updatedDevices = devices.stream()
                    .map(storeDevice -> {
                        if (deviceMatches(storeDevice, device)) {
                            storeDevice.setName(device.getName());
                        }
                        return storeDevice;
                    }).collect(Collectors.toList());
            devices = updatedDevices;
            deviceStoreResponse.setUpdated(true);
        } else {
            deviceStoreResponse.setUpdated(false);
        }
        return deviceStoreResponse;
    }

    private boolean deviceExists(Device device) {
        return devices.stream().anyMatch(device1 -> deviceMatches(device, device1));
    }

    private boolean deviceMatches(Device device, Device device1) {
        return device.getSerialNumber().equals(device1.getSerialNumber()) && device.getMachineCode().equals(device1.getMachineCode());
    }
}
