package com.gsvn.productservice.client.fallback;

import com.gsvn.productservice.client.InventorySkuClient;
import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.exc.AppException;
import com.gsvn.productservice.exc.ErrorCode;
import com.gsvn.productservice.model.internal.SkuGlobalConfigResponse;
import com.gsvn.productservice.model.internal.SkuGlobalSyncRequest;
import com.gsvn.productservice.model.internal.SkuSellableDTO;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InventorySkuClientFallbackFactory implements FallbackFactory<InventorySkuClient> {

    @Override
    public InventorySkuClient create(Throwable cause) {
        return new InventorySkuClient() {

            @Override
            public ApiResponse<Void> syncConfig(SkuGlobalSyncRequest request) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Inventory Service syncConfig failed. Cause: {}",
                        cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<Map<Long, SkuGlobalConfigResponse>> getBulkStatus(List<Long> skuIds) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Inventory Service getBulkStatus failed for SKUs: {}. Cause: {}",
                        skuIds, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<List<SkuSellableDTO>> checkSellable(List<Long> skuIds) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Inventory Service checkSellable failed for SKUs: {}. Cause: {}",
                        skuIds, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}