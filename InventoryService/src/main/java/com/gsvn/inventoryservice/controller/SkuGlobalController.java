package com.gsvn.inventoryservice.controller;

import com.gsvn.inventoryservice.model.dto.request.SkuGlobalSyncRequest;
import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.model.dto.response.SkuGlobalConfigResponse;
import com.gsvn.inventoryservice.service.SkuGlobalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class SkuGlobalController {

    private final SkuGlobalService skuGlobalService;

    @PutMapping("/internal/sync")
    public ApiResponse<Void> syncConfig(@Valid @RequestBody SkuGlobalSyncRequest request) {
        skuGlobalService.syncSkuConfig(request);
        return new ApiResponse<>(null);
    }
    @PostMapping("/internal/bulk-status")
    public ApiResponse<Map<Long, SkuGlobalConfigResponse>> getBulkStatus(@RequestBody List<Long> skuIds) {
        return new ApiResponse<>(skuGlobalService.getBulkStatusMap(skuIds));
    }
}