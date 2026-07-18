package com.gsvn.searchservice.client;

import com.gsvn.searchservice.common.ApiResponse;
import com.gsvn.searchservice.config.InternalFeignConfig;
import com.gsvn.searchservice.model.internal.SkuSearchResponse;
import com.gsvn.searchservice.model.internal.SkuSellableDTO;
import com.gsvn.searchservice.model.internal.SkuStockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "inventory-service",contextId = "inventory", path = "/api/v1", configuration = InternalFeignConfig.class)
public interface InventoryClient {

    @PostMapping("/inventory/internal/check-sellable")
    ApiResponse<List<SkuSellableDTO>> checkSellable(@RequestBody List<Long> skuIds);
    @GetMapping("/sku-stocks")
    ApiResponse<SkuStockResponse> getStock(
            @RequestParam("skuId") Long skuId,
            @RequestParam("warehouseId") Integer warehouseId
    );
    @GetMapping("/sku-stocks/all-warehouses")
    ApiResponse<List<SkuStockResponse>> getAllWarehousesStock(@RequestParam("skuId") Long skuId);
}