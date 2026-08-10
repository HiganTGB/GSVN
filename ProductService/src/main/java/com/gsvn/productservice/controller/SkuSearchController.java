package com.gsvn.productservice.controller;

import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.model.dto.SkuCartDetailsDTO;
import com.gsvn.productservice.model.dto.SkuSearchResponse;
import com.gsvn.productservice.model.dto.response.SkuResponse;
import com.gsvn.productservice.service.SkuSearchService;
import com.gsvn.productservice.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/skus")
@RequiredArgsConstructor
public class SkuSearchController {
    private final SkuSearchService skuSearchService;
    private final SkuService skuService;
    @GetMapping("/internal/search")
    public ApiResponse<List<SkuSearchResponse>> quickSearch(@RequestParam String keyword) {
        return new ApiResponse<>(skuSearchService.searchSkuForInventory(keyword));
    }

    @PostMapping("/internal/list")
    public ApiResponse<Map<Long,SkuSearchResponse>> getByIds(@RequestBody List<Long> skuIds) {
        return new ApiResponse<>(skuSearchService.getSkuMapByIds(skuIds));
    }
    @PostMapping("/internal/cart-details")
    public ApiResponse<List<SkuCartDetailsDTO>> getCartDetails(@RequestBody List<Long> skuIds) {
        return new ApiResponse<>(skuService.getCartDetails(skuIds));
    }
}