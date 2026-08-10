package com.gsvn.shipmentservice.client.fallback;

import com.gsvn.shipmentservice.client.InventoryClient;
import com.gsvn.shipmentservice.common.ApiResponse;
import com.gsvn.shipmentservice.exc.AppException;
import com.gsvn.shipmentservice.exc.ErrorCode;
import com.gsvn.shipmentservice.model.dto.internal.InventoryUpdateRequest;
import com.gsvn.shipmentservice.model.internal.WarehouseResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryClient> {

    @Override
    public InventoryClient create(Throwable cause) {
        return new InventoryClient() {

            @Override
            public ApiResponse<WarehouseResponse> getByCode(String code) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Inventory Service getByCode failed for warehouse code: {}. Cause: {}",
                        code, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<Void> processReadyToPick(InventoryUpdateRequest request) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Inventory Service processReadyToPick failed. Cause: {}",
                        cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<Void> processPacked(InventoryUpdateRequest request) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Inventory Service processPacked failed. Cause: {}",
                        cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}