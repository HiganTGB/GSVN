package com.gsvn.orderservice.client;

import com.gsvn.orderservice.common.ApiResponse;
import com.gsvn.orderservice.config.InternalFeignConfig;
import com.gsvn.orderservice.model.dto.internal.ShipmentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "shipment-service",path ="/api/v1/shipments",configuration = InternalFeignConfig.class)
public interface ShipmentFeignClient {

    @PostMapping("/internal")
    ApiResponse<Void> createShipment(@RequestBody ShipmentRequest request);
}