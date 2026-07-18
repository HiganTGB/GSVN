package com.gsvn.searchservice.client;

import com.gsvn.searchservice.common.ApiResponse;
import com.gsvn.searchservice.common.PageCursorResponse;
import com.gsvn.searchservice.model.internal.SkuResponse;
import com.gsvn.searchservice.model.internal.SkuSearchResponse;
import com.gsvn.searchservice.model.response.ProductCardDTO;
import com.gsvn.searchservice.model.response.ProductDetailDTO;
import com.gsvn.searchservice.model.response.SaleStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "product-service", path = "/api/v1")
public interface ProductClient {

    @GetMapping("/products/internal/{id}")
    ApiResponse<ProductDetailDTO> getDetail(@PathVariable Integer id);

    @GetMapping("/products/internal/search")
    ApiResponse<PageCursorResponse<ProductCardDTO>> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "brandIds", required = false) List<Integer> brandIds,
            @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<SaleStatus> saleStatuses,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String direction,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit
    );
    @GetMapping("/products/internal/{id}/skus")
    ApiResponse<List<SkuResponse>> getSkus(@PathVariable Integer id);
    @GetMapping("/skus/internal/search")
    ApiResponse<List<SkuSearchResponse>> quickSearch(@RequestParam String keyword);
}