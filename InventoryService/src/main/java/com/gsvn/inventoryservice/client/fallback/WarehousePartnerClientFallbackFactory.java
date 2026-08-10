package com.gsvn.inventoryservice.client.fallback;

import com.gsvn.inventoryservice.client.WarehousePartnerClient;
import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.exc.AppException;
import com.gsvn.inventoryservice.exc.ErrorCode;
import com.gsvn.inventoryservice.model.internal.WarehousePartnerRequest;
import com.gsvn.inventoryservice.model.internal.WarehousePartnerResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class WarehousePartnerClientFallbackFactory implements FallbackFactory<WarehousePartnerClient> {

    @Override
    public WarehousePartnerClient create(Throwable cause) {
        return new WarehousePartnerClient() {

            @Override
            public ApiResponse<WarehousePartnerResponse> savePartner(String code, WarehousePartnerRequest request) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Shipment Service failed when saving partner for warehouse code: {}. Cause: {}",
                        code, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<Void> deletePartner(String code, String name) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Shipment Service failed when deleting partner: {} for warehouse code: {}. Cause: {}",
                        name, code, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<List<WarehousePartnerResponse>> getPartners(String code) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Shipment Service failed when getting partners for warehouse code: {}. Cause: {}",
                        code, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<String> getDecryptedToken(String code, String name) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Shipment Service failed when getting decrypted token for partner: {} at warehouse: {}. Cause: {}",
                        name, code, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}