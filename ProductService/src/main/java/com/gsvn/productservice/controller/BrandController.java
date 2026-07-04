package com.gsvn.productservice.controller;

import com.gsvn.productservice.model.dto.request.BrandRequest;
import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.model.dto.response.BrandResponse;
import com.gsvn.productservice.common.PageResponse;
import com.gsvn.productservice.service.BrandService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandController {
    BrandService brandService;

    @PostMapping
    public ApiResponse<BrandResponse> create(@RequestBody @Valid BrandRequest request) {
        return new ApiResponse<>(brandService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<BrandResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid BrandRequest request) {
        return new ApiResponse<>(brandService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(brandService.getById(id));
    }


    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        brandService.delete(id);
        return new ApiResponse<>();
    }
    @GetMapping("/list")
    public ApiResponse<List<BrandResponse>> getList() {
        return new ApiResponse<>(brandService.getList());
    }
    @GetMapping("/search")
    public ApiResponse<PageResponse<BrandResponse>> getPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(brandService.getPage(keyword, sortBy,direction,page,size));
    }
}