package com.gsvn.productservice.controller;

import com.gsvn.productservice.model.dto.request.CategoryRequest;
import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.model.dto.response.CategoryResponse;
import com.gsvn.productservice.common.PageResponse;
import com.gsvn.productservice.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('category_read')")
    public ApiResponse<CategoryResponse> create(@RequestBody @Valid CategoryRequest request) {
        return new ApiResponse<>(categoryService.create(request));
    }

    @GetMapping("/tree")
    public ApiResponse<List<CategoryResponse>> getTree() {
        return new ApiResponse<>(categoryService.getCategoryTree());
    }

    @GetMapping("/list")
    public ApiResponse<List<CategoryResponse>> getList() {
        return new ApiResponse<>(categoryService.getList());
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(categoryService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('category_update')")
    public ApiResponse<CategoryResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid CategoryRequest request) {
        return new ApiResponse<>(categoryService.update(id, request));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('category_delete')")
    public ApiResponse<CategoryResponse> delete(
            @PathVariable Integer id) {
        categoryService.delete(id);
        return new ApiResponse<>();
    }


    @GetMapping("/search")
    public ApiResponse<PageResponse<CategoryResponse>> getPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(categoryService.getPage(keyword, page,size));
    }
}