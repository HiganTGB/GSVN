package com.gsvn.inventoryservice.controller;

import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.InventoryDTO;
import com.gsvn.inventoryservice.model.dto.SkuSellableDTO;
import com.gsvn.inventoryservice.model.dto.request.InventoryUpdateRequest;
import com.gsvn.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/search")
    public ApiResponse<PageResponse<InventoryDTO>> getInventory(
            @RequestParam(required = false) Integer warehouseId,
            @RequestParam(required = false) Integer skuId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updated_at") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        PageResponse<InventoryDTO> result = inventoryService.getInventoryList(
                warehouseId,
                skuId,
                page,
                size,
                sortField,
                sortDirection
        );
        return new ApiResponse<>(result);
    }

    @PostMapping("/internal/check-sellable")
    public ApiResponse<List<SkuSellableDTO>> checkSellable(@RequestBody List<Long> skuIds) {
        var result  = inventoryService.getSkusSellable(skuIds);
        return new ApiResponse<>(result);
    }

    @PostMapping("/internal/process-ready-to-pick")
    public ApiResponse<Void> processReadyToPick(@RequestBody InventoryUpdateRequest request) {
        inventoryService.processReadyToPick(
                request.getSkuCode(),
                request.getWarehouseId(),
                request.getQuantity()
        );
        return new ApiResponse<>("Global reserved moved to local", null);
    }

    @PostMapping("/internal/process-packed")
    public ApiResponse<Void> processPacked(@RequestBody InventoryUpdateRequest request) {
        inventoryService.processPacked(
                request.getSkuCode(),
                request.getWarehouseId(),
                request.getQuantity()
        );
        return new ApiResponse<>("Physical stock decreased", null);
    }
}