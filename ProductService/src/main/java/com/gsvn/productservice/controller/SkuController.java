package com.gsvn.productservice.controller;

import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.model.dto.SkuCartDetailsDTO;
import com.gsvn.productservice.model.dto.request.SkuRequest;
import com.gsvn.productservice.model.dto.response.SkuResponse;
import com.gsvn.productservice.service.SkuService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/skus")
@RequiredArgsConstructor
@Tag(name = "SKU Variant Management", description = "Endpoints for managing SKU variants associated with a master product")
public class SkuController {

    private final SkuService skuService;

    @Operation(summary = "Get SKUs by product ID", description = "Retrieves a list of all active SKU variants belonging to a specific master product.")
    @GetMapping
    public ApiResponse<List<SkuResponse>> getSkusByProduct(
            @Parameter(description = "ID of the master product") @PathVariable Integer productId) {
        return new ApiResponse<>(skuService.getSkusByProduct(productId));
    }

    @Operation(summary = "Create SKU variant", description = "Creates a new SKU variant for a specific master product.")
    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('product_update')")
    public ApiResponse<SkuResponse> createSku(
            @Parameter(description = "ID of the master product") @PathVariable Integer productId,
            @RequestBody @Valid SkuRequest request) {
        return new ApiResponse<>(skuService.createSku(productId, request));
    }

    @Operation(summary = "Update SKU variant", description = "Updates details of an existing SKU variant by its SKU ID.")
    @PutMapping("/{skuId}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('product_update')")
    public ApiResponse<SkuResponse> updateSku(
            @Parameter(description = "ID of the SKU variant to update") @PathVariable Long skuId,
            @RequestBody @Valid SkuRequest request,
            @Parameter(description = "ID of the master product") @PathVariable String productId) {
        return new ApiResponse<>(skuService.updateSku(skuId, request));
    }
}