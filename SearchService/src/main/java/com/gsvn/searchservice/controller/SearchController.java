package com.gsvn.searchservice.controller;

import com.gsvn.searchservice.common.ApiResponse;
import com.gsvn.searchservice.common.PageCursorResponse;
import com.gsvn.searchservice.model.internal.SkuResponse;
import com.gsvn.searchservice.model.internal.SkuSearchResponse;
import com.gsvn.searchservice.model.internal.SkuStockResponse;
import com.gsvn.searchservice.model.response.ProductCardDTO;
import com.gsvn.searchservice.model.response.ProductFullDetailDTO;
import com.gsvn.searchservice.model.response.SaleStatus;
import com.gsvn.searchservice.model.response.SkuInfoResponse;
import com.gsvn.searchservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/products")
    public ApiResponse<PageCursorResponse<ProductCardDTO>> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "brandIds", required = false) List<Integer> brandIds,
            @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "saleStatuses", required = false) List<SaleStatus> saleStatuses,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", required = false, defaultValue = "desc") String direction,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit
    ) {
        var data = searchService.search(
                keyword,
                brandIds,
                categoryIds,
                minPrice,
                maxPrice,
                saleStatuses,
                cursor,
                sortBy,
                direction,
                limit
        );
        return new ApiResponse<>(data);
    }
    @GetMapping("/products/{id}")
    public ApiResponse<ProductFullDetailDTO> getProductDetail(@PathVariable Integer id) {
        var data = searchService.getDetail(id);
        return new ApiResponse<>(data);
    }
    @GetMapping("/products/{id}/stock/warehouse/{warehouseId}")
    public ApiResponse<List<SkuStockResponse>> getProductStockByWarehouse(
            @PathVariable Integer id,
            @PathVariable Integer warehouseId) {
        var data = searchService.getProductStockByWarehouse(id, warehouseId);
        return new ApiResponse<>(data);
    }
    @GetMapping("/products/{id}/stock/all-warehouses")
    public ApiResponse<Map<Long, List<SkuStockResponse>>> getAllProductStocks(
            @PathVariable Integer id) {
        var data = searchService.getAllProductStocks(id);
        return new ApiResponse<>(data);
    }
    @GetMapping("/products/{id}/skus")
    ApiResponse<List<SkuResponse>> getSkus(@PathVariable Integer id)
    {
        return new ApiResponse<>(searchService.getSkusByProduct(id));
    }
    @GetMapping("/skus")
    public ApiResponse<List<SkuSearchResponse>> searchSkus(
            @RequestParam(value = "keyword", required = false) String keyword
    )
    {
        return new ApiResponse<>(searchService.searchSkus(keyword));
    }
    @GetMapping("/products/{id}/sku-infos")
    public ApiResponse<List<SkuInfoResponse>> getSkuInfos(
            @PathVariable Integer id,
            @RequestParam("currentWarehouseId") Integer warehouseId
    ) {
        var data = searchService.getSkuInfosForSearch(id, warehouseId);
        return new ApiResponse<>(data);
    }
}