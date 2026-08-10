package com.gsvn.shipmentservice.client.fallback;

import com.gsvn.shipmentservice.client.OrderClient;
import com.gsvn.shipmentservice.common.ApiResponse;
import com.gsvn.shipmentservice.exc.AppException;
import com.gsvn.shipmentservice.exc.ErrorCode;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderClientFallbackFactory implements FallbackFactory<OrderClient> {

    @Override
    public OrderClient create(Throwable cause) {
        return new OrderClient() {

            @Override
            public ApiResponse<Boolean> updateStatus(String code, String status) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Order Service updateStatus failed for orderCode: {}, status: {}. Cause: {}",
                        code, status, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}