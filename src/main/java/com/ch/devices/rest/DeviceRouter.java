package com.ch.devices.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class DeviceRouter {
    @Bean
    public RouterFunction<ServerResponse> serviceDiscover(DeviceHandler deviceHandler) {
        return route(GET("/device/{serialNumber}/{machineCode}"), deviceHandler::getDevice)
                .andRoute(POST("/device"), deviceHandler::createDevice)
                .andRoute(PUT("/device"), deviceHandler::updateDevice);
    }
}
