package com.gsvn.inventoryservice.client;

import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.config.InternalFeignConfig;
import com.gsvn.inventoryservice.model.internal.SkuSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "product-service", contextId = "skuSearch", path = "/api/v1/skus",configuration = InternalFeignConfig.class)
public interface SkuSearchInternalClient {
    @PostMapping("/internal/list")
    ApiResponse<Map<Long,SkuSearchResponse>> getByIds(@RequestBody List<Long> skuIds);
}