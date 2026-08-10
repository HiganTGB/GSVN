package com.gsvn.shipmentservice.controller;

import com.gsvn.shipmentservice.common.ApiResponse;
import com.gsvn.shipmentservice.model.dto.request.WarehousePartnerRequest;
import com.gsvn.shipmentservice.model.dto.response.WarehousePartnerResponse;
import com.gsvn.shipmentservice.service.WarehousePartnerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouseconfig/internal")
@RequiredArgsConstructor
@Tag(name = "Warehouse Partner Config (Internal)", description = "Internal endpoints for configuring logistics carrier partner tokens and integration credentials per warehouse code")
public class WarehousePartnerController {

    private final WarehousePartnerService warehouseService;

    @Operation(summary = "Save or update warehouse partner integration", description = "Saves or updates integration tokens and API credentials for a logistics carrier assigned to a specific warehouse code.")
    @PostMapping("/{code}/partners")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('warehouse_permission'))")
    public ApiResponse<WarehousePartnerResponse> savePartner(
            @Parameter(description = "Unique code of the warehouse") @PathVariable String code,
            @RequestBody @Valid WarehousePartnerRequest request
    ) {
        return new ApiResponse<>(warehouseService.savePartnerToken(request, code));
    }

    @Operation(summary = "Delete warehouse partner integration", description = "Removes a logistics partner configuration from a warehouse by warehouse code and partner name.")
    @DeleteMapping("/{code}/partners/{name}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('warehouse_permission'))")
    public ApiResponse<Void> deletePartner(
            @Parameter(description = "Unique code of the warehouse") @PathVariable String code,
            @Parameter(description = "Name of the logistics partner (e.g., GHN, GHTK)") @PathVariable String name
    ) {
        warehouseService.deletePartner(code, name);
        return new ApiResponse<>();
    }

    @Operation(summary = "Get partners by warehouse code", description = "Retrieves a list of active logistics carrier partner configurations for a specific warehouse code.")
    @GetMapping("/{code}/partners")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('warehouse_permission'))")
    public ApiResponse<List<WarehousePartnerResponse>> getPartners(
            @Parameter(description = "Unique code of the warehouse") @PathVariable String code) {
        return new ApiResponse<>(warehouseService.getPartnersByWarehouseId(code));
    }

    @Operation(summary = "Get decrypted partner token", description = "Retrieves the decrypted authentication token for a specific logistics partner at a warehouse.")
    @GetMapping("/{code}/partners/{name}/token")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('warehouse_permission'))")
    public ApiResponse<String> getDecryptedToken(
            @Parameter(description = "Unique code of the warehouse") @PathVariable String code,
            @Parameter(description = "Name of the logistics partner") @PathVariable String name) {
        return new ApiResponse<>(warehouseService.getDecryptedToken(code, name));
    }
}