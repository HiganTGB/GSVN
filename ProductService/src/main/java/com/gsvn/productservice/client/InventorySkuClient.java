package com.gsvn.productservice.client;


import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.config.InternalFeignConfig;
import com.gsvn.productservice.model.internal.SkuGlobalConfigResponse;
import com.gsvn.productservice.model.internal.SkuGlobalSyncRequest;
import com.gsvn.productservice.model.internal.SkuSellableDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "inventory-service",contextId = "inventory",path ="/api/v1/inventory", configuration = InternalFeignConfig.class )
public interface InventorySkuClient {

    @PutMapping("/internal/sync")
    ApiResponse<Void> syncConfig(@RequestBody SkuGlobalSyncRequest request);

    @PostMapping("/internal/bulk-status")
    ApiResponse<Map<Long, SkuGlobalConfigResponse>> getBulkStatus(@RequestBody List<Long> skuIds);

    @PostMapping("/internal/check-sellable")
    ApiResponse<List<SkuSellableDTO>> checkSellable(@RequestBody List<Long> skuIds);
}