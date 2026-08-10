package com.gsvn.productservice.controller;

import com.gsvn.productservice.model.dto.request.CategoryRequest;
import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.model.dto.response.CategoryResponse;
import com.gsvn.productservice.common.PageResponse;
import com.gsvn.productservice.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Category Management", description = "Endpoints for managing product categories, hierarchical category trees, and catalog listings")
public class CategoryController {

    CategoryService categoryService;

    @Operation(summary = "Create category", description = "Creates a new product category record in the catalog.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('category_create'))")
    public ApiResponse<CategoryResponse> create(@RequestBody @Valid CategoryRequest request) {
        return new ApiResponse<>(categoryService.create(request));
    }

    @Operation(summary = "Get category tree structure", description = "Retrieves the full hierarchical tree of categories for menu rendering and navigation.")
    @GetMapping("/tree")
    public ApiResponse<List<CategoryResponse>> getTree() {
        return new ApiResponse<>(categoryService.getCategoryTree());
    }

    @Operation(summary = "Get category list", description = "Retrieves a flat list of all active categories for dropdown selection.")
    @GetMapping("/list")
    public ApiResponse<List<CategoryResponse>> getList() {
        return new ApiResponse<>(categoryService.getList());
    }

    @Operation(summary = "Get category by ID", description = "Retrieves details of a specific category by ID.")
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getById(
            @Parameter(description = "ID of the category") @PathVariable Integer id) {
        return new ApiResponse<>(categoryService.getById(id));
    }

    @Operation(summary = "Update category", description = "Updates details of an existing category by ID.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('category_update'))")
    public ApiResponse<CategoryResponse> update(
            @Parameter(description = "ID of the category") @PathVariable Integer id,
            @RequestBody @Valid CategoryRequest request) {
        return new ApiResponse<>(categoryService.update(id, request));
    }

    @Operation(summary = "Delete category", description = "Deletes a category by ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('category_delete'))")
    public ApiResponse<CategoryResponse> delete(
            @Parameter(description = "ID of the category to delete") @PathVariable Integer id) {
        categoryService.delete(id);
        return new ApiResponse<>();
    }

    @Operation(summary = "Search categories with pagination", description = "Retrieves a paginated list of categories filtered by keyword.")
    @GetMapping("/search")
    public ApiResponse<PageResponse<CategoryResponse>> getPage(
            @Parameter(description = "Keyword to filter categories by name or code")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Page number (1-based index)")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(categoryService.getPage(keyword, page, size));
    }
}