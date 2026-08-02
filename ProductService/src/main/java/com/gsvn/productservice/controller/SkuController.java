package com.gsvn.productservice.controller;

import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.model.dto.SkuCartDetailsDTO;
import com.gsvn.productservice.model.dto.request.SkuRequest;
import com.gsvn.productservice.model.dto.response.SkuResponse;
import com.gsvn.productservice.service.SkuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/skus")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @GetMapping
    public ApiResponse<List<SkuResponse>> getSkusByProduct(@PathVariable Integer  productId) {
        return new ApiResponse<>(skuService.getSkusByProduct(productId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('product_update')")
    public ApiResponse<SkuResponse> createSku(
            @PathVariable Integer productId,
            @RequestBody @Valid SkuRequest request) {
        return new ApiResponse<>(skuService.createSku(productId, request));
    }

    @PutMapping("/{skuId}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('product_update')")
    public ApiResponse<SkuResponse> updateSku(
            @PathVariable Long skuId,
            @RequestBody @Valid SkuRequest request, @PathVariable String productId) {
        return new ApiResponse<>(skuService.updateSku( skuId, request));
    }

}