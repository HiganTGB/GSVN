package com.gsvn.productservice.controller;

import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.model.dto.request.ProductVariantSyncRequest;
import com.gsvn.productservice.model.dto.response.VariantResponse;
import com.gsvn.productservice.service.VariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/variants")
@RequiredArgsConstructor
public class VariantController {

    private final VariantService variantService;

    @PutMapping("/sync")
    public ApiResponse<Void> syncVariants(
            @PathVariable Integer productId,
            @RequestBody @Valid List<ProductVariantSyncRequest.VariantUpdateDto> request) {

        variantService.syncVariants(productId, request);
        return new ApiResponse<>();
    }
    @GetMapping()
    public ApiResponse<List<VariantResponse>> getVariantInfoForProduct(@PathVariable Integer productId)
    {
        return new ApiResponse<>(variantService.getVariantByProduct(productId));
    }
}