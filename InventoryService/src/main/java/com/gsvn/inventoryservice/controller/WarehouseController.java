package com.gsvn.inventoryservice.controller;

import com.gsvn.inventoryservice.model.internal.WarehousePartnerRequest;
import com.gsvn.inventoryservice.model.dto.request.WarehouseRequest;
import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.internal.WarehousePartnerResponse;
import com.gsvn.inventoryservice.model.dto.response.WarehouseResponse;
import com.gsvn.inventoryservice.service.WarehouseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouse Management", description = "Endpoints for managing physical warehouses, logistics partner token integrations, and internal service lookups")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Operation(summary = "Create warehouse", description = "Creates a new physical warehouse location in the system.")
    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_create')")
    public ApiResponse<WarehouseResponse> create(@RequestBody @Valid WarehouseRequest request) {
        return new ApiResponse<>(warehouseService.create(request));
    }

    @Operation(summary = "Update warehouse", description = "Updates details of an existing warehouse location by ID.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_update')")
    public ApiResponse<WarehouseResponse> update(
            @Parameter(description = "ID of the warehouse") @PathVariable Integer id,
            @RequestBody @Valid WarehouseRequest request) {
        return new ApiResponse<>(warehouseService.update(id, request));
    }

    @Operation(summary = "Get warehouse by ID", description = "Retrieves detailed profile information for a specific warehouse by ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_read')")
    public ApiResponse<WarehouseResponse> getById(
            @Parameter(description = "ID of the warehouse") @PathVariable Integer id) {
        return new ApiResponse<>(warehouseService.getById(id));
    }

    @Operation(summary = "Delete warehouse", description = "Deletes or deactivates a warehouse location by ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_delete')")
    public ApiResponse<Void> delete(
            @Parameter(description = "ID of the warehouse to delete") @PathVariable Integer id) {
        warehouseService.delete(id);
        return new ApiResponse<>();
    }

    @Operation(summary = "Search warehouses with pagination", description = "Retrieves a paginated list of warehouses filtered by keyword with dynamic sorting.")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_read')")
    public ApiResponse<PageResponse<WarehouseResponse>> getPage(
            @Parameter(description = "Keyword to filter warehouses by name, code, or address")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Field name to sort results by")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sorting direction: 'asc' or 'desc'")
            @RequestParam(defaultValue = "desc") String direction,

            @Parameter(description = "Page number (1-based index)")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {

        return new ApiResponse<>(warehouseService.getPage(keyword, sortBy, direction, page, size));
    }

    @Operation(summary = "Get all warehouses", description = "Retrieves a complete list of all registered warehouses without pagination.")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_read')")
    public ApiResponse<List<WarehouseResponse>> getAll() {
        return new ApiResponse<>(warehouseService.getAll());
    }

    @Operation(summary = "Save or update partner integration", description = "Saves integration tokens or credentials for a shipping/logistics partner assigned to a warehouse.")
    @PostMapping("/{id}/partners")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_permission')")
    public ApiResponse<WarehousePartnerResponse> savePartner(
            @Parameter(description = "ID of the warehouse") @PathVariable Integer id,
            @RequestBody @Valid WarehousePartnerRequest request
    ) {
        return new ApiResponse<>(warehouseService.savePartnerToken(request, id));
    }

    @Operation(summary = "Delete partner integration", description = "Removes a logistics partner configuration from a specific warehouse by partner name.")
    @DeleteMapping("/{id}/partners/{name}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_permission')")
    public ApiResponse<Void> deletePartner(
            @Parameter(description = "ID of the warehouse") @PathVariable Integer id,
            @Parameter(description = "Name of the partner integration to delete") @PathVariable String name
    ) {
        warehouseService.deletePartner(id, name);
        return new ApiResponse<>();
    }

    @Operation(summary = "Get warehouse partners", description = "Retrieves a list of all active logistics partner configurations for a specific warehouse.")
    @GetMapping("/{id}/partners")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_permission')")
    public ApiResponse<List<WarehousePartnerResponse>> getPartners(
            @Parameter(description = "ID of the warehouse") @PathVariable Integer id) {
        return new ApiResponse<>(warehouseService.getPartnersByWarehouseId(id));
    }

    @Operation(summary = "Get decrypted partner token", description = "Retrieves the decrypted integration token for a specific partner at a warehouse.")
    @GetMapping("/{id}/partners/{name}/token")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_permission')")
    public ApiResponse<String> getDecryptedToken(
            @Parameter(description = "ID of the warehouse") @PathVariable Integer id,
            @Parameter(description = "Name of the partner") @PathVariable String name) {
        return new ApiResponse<>(warehouseService.getDecryptedToken(id, name));
    }

    @Operation(summary = "Get warehouse by code (Internal)", description = "Internal endpoint for inter-service communication to fetch warehouse details by code.")
    @GetMapping("/internal/{code}")
    public ApiResponse<WarehouseResponse> getByCode(
            @Parameter(description = "Unique code of the warehouse") @PathVariable String code) {
        return new ApiResponse<>(warehouseService.getByCode(code));
    }
}