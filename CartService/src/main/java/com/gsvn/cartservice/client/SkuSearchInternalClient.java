package com.gsvn.cartservice.client;


import com.gsvn.cartservice.client.fallback.SkuSearchInternalClientFallbackFactory;
import com.gsvn.cartservice.common.ApiResponse;
import com.gsvn.cartservice.config.InternalFeignConfig;
import com.gsvn.cartservice.model.internal.SkuCartDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "product-service",
        contextId = "skus",
        path = "/api/v1/skus",
        configuration = InternalFeignConfig.class,
        fallbackFactory = SkuSearchInternalClientFallbackFactory.class
)
public interface SkuSearchInternalClient {
    @PostMapping("/internal/cart-details")
    public ApiResponse<List<SkuCartDetailsDTO>> getCartDetails(@RequestBody List<Long> skuIds);
}