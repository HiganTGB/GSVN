package com.gsvn.inventoryservice.controller;

import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.InventoryDTO;
import com.gsvn.inventoryservice.model.dto.SkuSellableDTO;
import com.gsvn.inventoryservice.model.dto.request.InventoryUpdateRequest;
import com.gsvn.inventoryservice.service.InventoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Stock Management", description = "Endpoints for managing warehouse stock levels, checking sellable quantities, and internal order fulfillment state updates")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Search warehouse inventory", description = "Retrieves a paginated list of stock levels filtered by warehouse or SKU ID with dynamic sorting.")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('all') or hasAuthority('stock_read')")
    public ApiResponse<PageResponse<InventoryDTO>> getInventory(
            @Parameter(description = "Filter by warehouse ID") @RequestParam(required = false) Integer warehouseId,
            @Parameter(description = "Filter by SKU ID") @RequestParam(required = false) Integer skuId,
            @Parameter(description = "Page number (1-based index)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort results by") @RequestParam(defaultValue = "updated_at") String sortField,
            @Parameter(description = "Sort direction: 'ASC' or 'DESC'") @RequestParam(defaultValue = "DESC") String sortDirection
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

    @Operation(summary = "Check sellable stock (Internal)", description = "Internal endpoint for Order Service to check sellable stock availability across a list of SKU IDs.")
    @PostMapping("/internal/check-sellable")
    public ApiResponse<List<SkuSellableDTO>> checkSellable(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of SKU IDs to check availability")
            @RequestBody List<Long> skuIds) {
        var result = inventoryService.getSkusSellable(skuIds);
        return new ApiResponse<>(result);
    }

    @Operation(summary = "Process ready-to-pick stock (Internal)", description = "Internal endpoint to move global reserved stock to local warehouse picking allocation.")
    @PostMapping("/internal/process-ready-to-pick")
    public ApiResponse<Void> processReadyToPick(@RequestBody InventoryUpdateRequest request) {
        inventoryService.processReadyToPick(
                request.getSkuCode(),
                request.getWarehouseId(),
                request.getQuantity()
        );
        return new ApiResponse<>("Global reserved moved to local", null);
    }

    @Operation(summary = "Process packed stock / Decrease physical inventory (Internal)", description = "Internal endpoint to decrease actual physical stock once items are packed for shipping.")
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