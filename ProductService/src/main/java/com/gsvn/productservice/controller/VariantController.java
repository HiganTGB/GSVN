package com.gsvn.productservice.controller;

import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.model.dto.request.ProductVariantSyncRequest;
import com.gsvn.productservice.model.dto.response.VariantResponse;
import com.gsvn.productservice.service.VariantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/variants")
@RequiredArgsConstructor
@Tag(name = "Product Variant Management", description = "Endpoints for managing product attributes, variant combinations, and batch synchronization for a master product")
public class VariantController {

    private final VariantService variantService;

    @Operation(summary = "Synchronize product variants", description = "Performs a batch update and synchronization of variant option combinations (e.g., Size, Color) for a specific master product.")
    @PutMapping("/sync")
    public ApiResponse<Void> syncVariants(
            @Parameter(description = "ID of the master product") @PathVariable Integer productId,
            @RequestBody @Valid List<ProductVariantSyncRequest.VariantUpdateDto> request) {

        variantService.syncVariants(productId, request);
        return new ApiResponse<>();
    }

    @Operation(summary = "Get variants by product ID", description = "Retrieves all variant attributes and option configurations for a specific master product.")
    @GetMapping()
    public ApiResponse<List<VariantResponse>> getVariantInfoForProduct(
            @Parameter(description = "ID of the master product") @PathVariable Integer productId) {
        return new ApiResponse<>(variantService.getVariantByProduct(productId));
    }
}