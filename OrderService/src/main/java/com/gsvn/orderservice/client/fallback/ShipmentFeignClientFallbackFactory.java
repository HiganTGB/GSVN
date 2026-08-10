package com.gsvn.orderservice.client.fallback;

import com.gsvn.orderservice.client.ShipmentFeignClient;
import com.gsvn.orderservice.common.ApiResponse;
import com.gsvn.orderservice.exc.AppException;
import com.gsvn.orderservice.exc.ErrorCode;
import com.gsvn.orderservice.model.dto.internal.ShipmentRequest;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShipmentFeignClientFallbackFactory implements FallbackFactory<ShipmentFeignClient> {

    @Override
    public ShipmentFeignClient create(Throwable cause) {
        return new ShipmentFeignClient() {
            @Override
            public ApiResponse<Void> createShipment(ShipmentRequest request) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Shipment Service call failed when creating shipment for orderCode/orderId: {}. Cause: {}",
                        request != null ? request.getOrderId() : "N/A", cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}