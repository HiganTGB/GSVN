package com.gsvn.shipmentservice.client;

import com.gsvn.shipmentservice.client.fallback.OrderClientFallbackFactory;
import com.gsvn.shipmentservice.common.ApiResponse;
import com.gsvn.shipmentservice.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "order-service",
        contextId = "orderClient",
        path = "/api/v1/orders",
        configuration = InternalFeignConfig.class,
        fallbackFactory = OrderClientFallbackFactory.class
)
public interface OrderClient {

    @PutMapping("/internal/{code}/updateShipment")
    ApiResponse<Boolean> updateStatus(
            @PathVariable("code") String code,
            @RequestParam("status") String status
    );
}