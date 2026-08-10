package com.gsvn.inventoryservice.controller;

import com.gsvn.inventoryservice.model.dto.request.SupplierRequest;
import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.response.SupplierResponse;
import com.gsvn.inventoryservice.service.SupplierService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Management", description = "Endpoints for managing inventory suppliers, contacts, status, and directory listings")
public class SupplierController {

    private final SupplierService supplierService;

    @Operation(summary = "Create supplier", description = "Creates a new inventory supplier profile.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('supplier_create'))")
    public ApiResponse<SupplierResponse> create(@RequestBody @Valid SupplierRequest request) {
        return new ApiResponse<>(supplierService.create(request));
    }

    @Operation(summary = "Update supplier", description = "Updates details of an existing supplier by ID.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('supplier_update'))")
    public ApiResponse<SupplierResponse> update(
            @Parameter(description = "ID of the supplier") @PathVariable Integer id,
            @RequestBody @Valid SupplierRequest request) {
        return new ApiResponse<>(supplierService.update(id, request));
    }

    @Operation(summary = "Get supplier by ID", description = "Retrieves detailed information of a specific supplier by ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('supplier_read'))")
    public ApiResponse<SupplierResponse> getById(
            @Parameter(description = "ID of the supplier") @PathVariable Integer id) {
        return new ApiResponse<>(supplierService.getById(id));
    }

    @Operation(summary = "Delete supplier", description = "Deletes a supplier record by ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('supplier_delete'))")
    public ApiResponse<Void> delete(
            @Parameter(description = "ID of the supplier to delete") @PathVariable Integer id) {
        supplierService.delete(id);
        return new ApiResponse<>();
    }

    @Operation(summary = "Search suppliers with pagination", description = "Retrieves a paginated list of suppliers filtered by keyword and active status with dynamic sorting.")
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('supplier_read'))")
    public ApiResponse<PageResponse<SupplierResponse>> getPage(
            @Parameter(description = "Keyword to search by supplier name, code, phone, or email")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Filter by active status (true for active, false for inactive)")
            @RequestParam(required = false) Boolean isActive,

            @Parameter(description = "Field name to sort results by")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sorting direction: 'asc' or 'desc'")
            @RequestParam(defaultValue = "desc") String direction,

            @Parameter(description = "Page number (1-based index)")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {

        return new ApiResponse<>(supplierService.getPage(keyword, isActive, sortBy, direction, page, size));
    }

    @Operation(summary = "Get all suppliers", description = "Retrieves a complete list of all suppliers without pagination.")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('supplier_read'))")
    public ApiResponse<List<SupplierResponse>> getAll() {
        return new ApiResponse<>(supplierService.getAll());
    }
}