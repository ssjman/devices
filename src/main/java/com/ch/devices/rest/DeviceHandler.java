package com.ch.devices.rest;

import com.ch.devices.model.Device;
import com.ch.devices.model.ErrorResponse;
import com.ch.devices.store.DeviceStore;
import com.ch.devices.store.DeviceStoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@RequiredArgsConstructor
@Component
public class DeviceHandler {
    private final DeviceStore deviceStore;

    public Mono<ServerResponse> getDevice(ServerRequest request) {
        Device device = deviceStore.getDevice(request.pathVariable("serialNumber"), request.pathVariable("machineCode"));
        if (device != null) {
            return ok()
                    .bodyValue(device);
        } else {
            return notFound().build();
        }
    }

    public Mono<ServerResponse> createDevice(ServerRequest request) {
        return request.bodyToMono(Device.class)
                .flatMap(this::createDevice);
    }

    public Mono<ServerResponse> updateDevice(ServerRequest request) {
        return request.bodyToMono(Device.class)
                .flatMap(this::updateDevice);
    }

    private Mono<ServerResponse> createDevice(Device device) {
        ErrorResponse errorResponse = validateDevice(device);
        if (StringUtils.hasLength(errorResponse.getErrorCode())) {
            return badRequest().bodyValue(errorResponse);
        }
        DeviceStoreResponse deviceStoreResponse = deviceStore.createDevice(device);
        if (deviceStoreResponse.isCreated()) {
            return ServerResponse.status(HttpStatus.CREATED).build();
        } else {
            return ok().build();
        }
    }

    private Mono<ServerResponse> updateDevice(Device device) {
        ErrorResponse errorResponse = validateDevice(device);
        if (StringUtils.hasLength(errorResponse.getErrorCode())) {
            return badRequest().bodyValue(errorResponse);
        }

        DeviceStoreResponse deviceStoreResponse = deviceStore.updateDevice(device);
        if (deviceStoreResponse.isUpdated()) {
            return ok().build();
        } else {
            return badRequest().bodyValue(new ErrorResponse("serial.number.not.found", "ER004", "The serial number does not match our records."));
        }
    }

    private ErrorResponse validateDevice(Device device) {
        if (!isValidSerialNumber(device)) {
            return new ErrorResponse("serial.number.invalid", "ER003", "The serial number entered can include a - z, A - Z, 0 - 9 and hyphen. Please correct your entry.");
        }
        if (!hasMachineCode(device)) {
            return new ErrorResponse("smachine.code.invalid", "ER001", "The machine code is incorrect. Check the Machine code you provided and try again.");
        }
        return new ErrorResponse();
    }

    private boolean hasMachineCode(Device device) {
        return StringUtils.hasLength(device.getMachineCode());
    }

    private boolean isValidSerialNumber(Device device) {
        return device.getSerialNumber().matches("([1-9])([1-9])-([1-9])([1-9])([1-9])([1-9])") ||
                device.getSerialNumber().matches("([1-9])([1-9])([1-9])([1-9])([1-9])([1-9])([1-9])-([1-9])([1-9])([1-9])([1-9])([1-9])") ||
                device.getSerialNumber().matches("([1-9])-([1-9])([1-9])([1-9])([1-9])([1-9])([1-9])([1-9])([1-9])");
    }


}
