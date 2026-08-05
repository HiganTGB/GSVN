package com.gsvn.productservice.controller;

import com.gsvn.productservice.model.dto.request.BrandRequest;
import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.model.dto.response.BrandResponse;
import com.gsvn.productservice.common.PageResponse;
import com.gsvn.productservice.service.BrandService;

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
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Brand Management", description = "Endpoints for managing product brands and catalog brand listings")
public class BrandController {

    BrandService brandService;

    @Operation(summary = "Create brand", description = "Creates a new product brand record in the catalog.")
    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('brand_read')")
    public ApiResponse<BrandResponse> create(@RequestBody @Valid BrandRequest request) {
        return new ApiResponse<>(brandService.create(request));
    }

    @Operation(summary = "Update brand", description = "Updates an existing brand profile by ID.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('brand_update')")
    public ApiResponse<BrandResponse> update(
            @Parameter(description = "ID of the brand") @PathVariable Integer id,
            @RequestBody @Valid BrandRequest request) {
        return new ApiResponse<>(brandService.update(id, request));
    }

    @Operation(summary = "Get brand by ID", description = "Retrieves details of a specific brand by ID.")
    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> getById(
            @Parameter(description = "ID of the brand") @PathVariable Integer id) {
        return new ApiResponse<>(brandService.getById(id));
    }

    @Operation(summary = "Delete brand", description = "Deletes a brand record by ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('brand_delete')")
    public ApiResponse<Void> delete(
            @Parameter(description = "ID of the brand to delete") @PathVariable Integer id) {
        brandService.delete(id);
        return new ApiResponse<>();
    }

    @Operation(summary = "Get brand list", description = "Retrieves an unpaginated list of all active brands for dropdowns and catalogs.")
    @GetMapping("/list")
    public ApiResponse<List<BrandResponse>> getList() {
        return new ApiResponse<>(brandService.getList());
    }

    @Operation(summary = "Search brands with pagination", description = "Retrieves a paginated list of brands filtered by keyword with dynamic sorting.")
    @GetMapping("/search")
    public ApiResponse<PageResponse<BrandResponse>> getPage(
            @Parameter(description = "Keyword to filter brands by name or code")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Field name to sort results by")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sorting direction: 'asc' or 'desc'")
            @RequestParam(defaultValue = "desc") String direction,

            @Parameter(description = "Page number (1-based index)")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(brandService.getPage(keyword, sortBy, direction, page, size));
    }
}