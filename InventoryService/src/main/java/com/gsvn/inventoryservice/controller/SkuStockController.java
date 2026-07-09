package com.gsvn.inventoryservice.controller;

import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.model.dto.response.SkuStockResponse;
import com.gsvn.inventoryservice.service.SkuStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sku-stocks")
@RequiredArgsConstructor
public class SkuStockController {

    private final SkuStockService skuStockService;
    @GetMapping
    public ApiResponse<SkuStockResponse> getStock(
            @RequestParam Long skuId,
            @RequestParam Integer warehouseId) {

        return new ApiResponse<>(skuStockService.getStock(skuId, warehouseId));
    }
    @GetMapping("/all-warehouses")
    public ApiResponse<List<SkuStockResponse>> getAllWarehousesStock(@RequestParam("skuId") Long skuId){
        return new ApiResponse<>(skuStockService.getStockAll(skuId));
    }
}