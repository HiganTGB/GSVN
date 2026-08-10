package com.gsvn.productservice.controller;

import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.common.PageCursorResponse;
import com.gsvn.productservice.model.dto.ProductCardDTO;
import com.gsvn.productservice.model.dto.ProductDetailDTO;
import com.gsvn.productservice.model.entity.SaleStatus;
import com.gsvn.productservice.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;


    @GetMapping("/internal/{id}")
    public ApiResponse<ProductDetailDTO> getDetail(@PathVariable Integer id) {
        return new ApiResponse<>(productSearchService.getDetail(id));
    }

    @GetMapping("/internal/search")
    public ApiResponse<PageCursorResponse<ProductCardDTO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Integer> brandIds,
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<SaleStatus> saleStatuses,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "20") Integer limit) {

        return new ApiResponse<>(productSearchService.search(
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
        ));
    }
}