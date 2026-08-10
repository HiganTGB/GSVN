package com.gsvn.inventoryservice.client.fallback;

import com.gsvn.inventoryservice.client.StaffServiceFeignClient;
import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.exc.AppException;
import com.gsvn.inventoryservice.exc.ErrorCode;
import com.gsvn.inventoryservice.model.internal.StaffResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StaffServiceFeignClientFallbackFactory implements FallbackFactory<StaffServiceFeignClient> {

    @Override
    public StaffServiceFeignClient create(Throwable cause) {
        return new StaffServiceFeignClient() {
            @Override
            public ApiResponse<StaffResponse> getInternalById(Long id) {
                log.error("[CircuitBreaker OPEN/FALLBACK] HRM Service call failed when fetching staff info for id: {}. Cause: {}",
                        id, cause.getMessage());

                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}