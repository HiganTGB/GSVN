package com.gsvn.inventoryservice.controller;

import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.model.dto.response.SkuStockResponse;
import com.gsvn.inventoryservice.service.SkuStockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sku-stocks")
@RequiredArgsConstructor
@Tag(name = "SKU Stock Management", description = "Endpoints for checking SKU stock levels per warehouse and across all warehouses")
public class SkuStockController {

    private final SkuStockService skuStockService;

    @Operation(summary = "Get SKU stock in specific warehouse", description = "Retrieves current stock availability and reserved quantity for a specific SKU in a designated warehouse.")
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('stock_read'))")
    public ApiResponse<SkuStockResponse> getStock(
            @Parameter(description = "ID of the SKU") @RequestParam Long skuId,
            @Parameter(description = "ID of the target warehouse") @RequestParam Integer warehouseId) {

        return new ApiResponse<>(skuStockService.getStock(skuId, warehouseId));
    }

    @Operation(summary = "Get SKU stock across all warehouses", description = "Retrieves a list of stock availability for a specific SKU across all registered warehouses.")
    @GetMapping("/all-warehouses")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('stock_read'))")
    public ApiResponse<List<SkuStockResponse>> getAllWarehousesStock(
            @Parameter(description = "ID of the SKU") @RequestParam("skuId") Long skuId) {
        return new ApiResponse<>(skuStockService.getStockAll(skuId));
    }
}