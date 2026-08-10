package com.gsvn.cartservice.client.fallback;

import com.gsvn.cartservice.client.SkuSearchInternalClient;
import com.gsvn.cartservice.common.ApiResponse;
import com.gsvn.cartservice.exc.AppException;
import com.gsvn.cartservice.exc.ErrorCode;
import com.gsvn.cartservice.model.internal.SkuCartDetailsDTO;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SkuSearchInternalClientFallbackFactory implements FallbackFactory<SkuSearchInternalClient> {

    @Override
    public SkuSearchInternalClient create(Throwable cause) {
        return new SkuSearchInternalClient() {
            @Override
            public ApiResponse<List<SkuCartDetailsDTO>> getCartDetails(List<Long> skuIds) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Product Service call failed when fetching cart details for SKUs: {}. Cause: {}",
                        skuIds, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}