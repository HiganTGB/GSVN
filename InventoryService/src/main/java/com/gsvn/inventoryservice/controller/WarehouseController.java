package com.gsvn.inventoryservice.controller;

import com.gsvn.inventoryservice.model.internal.WarehousePartnerRequest;
import com.gsvn.inventoryservice.model.dto.request.WarehouseRequest;
import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.internal.WarehousePartnerResponse;
import com.gsvn.inventoryservice.model.dto.response.WarehouseResponse;
import com.gsvn.inventoryservice.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_create')")
    public ApiResponse<WarehouseResponse> create(@RequestBody @Valid WarehouseRequest request) {
        return new ApiResponse<>(warehouseService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_update')")
    public ApiResponse<WarehouseResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid WarehouseRequest request) {
        return new ApiResponse<>(warehouseService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_read')")
    public ApiResponse<WarehouseResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(warehouseService.getById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_delete')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        warehouseService.delete(id);
        return new ApiResponse<>();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_read')")
    public ApiResponse<PageResponse<WarehouseResponse>> getPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){

        return new ApiResponse<>(warehouseService.getPage(keyword,sortBy,direction, page, size));
    }
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_read')")
    public ApiResponse<List<WarehouseResponse>> getAll(){
        return new ApiResponse<>(warehouseService.getAll());
    }


    @PostMapping("/{id}/partners")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_permission')")
    public ApiResponse<WarehousePartnerResponse> savePartner(
            @PathVariable Integer id,
            @RequestBody @Valid WarehousePartnerRequest request
    ) {
        return new ApiResponse<>(warehouseService.savePartnerToken(request, id));
    }

    @DeleteMapping("/{id}/partners/{name}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_permission')")
    public ApiResponse<Void> deletePartner(
            @PathVariable Integer id,
            @PathVariable String name
    ) {
        warehouseService.deletePartner(id, name);
        return new ApiResponse<>();
    }
    @GetMapping("/{id}/partners")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_permission')")
    public ApiResponse<List<WarehousePartnerResponse>> getPartners(@PathVariable Integer id) {
        return new ApiResponse<>(warehouseService.getPartnersByWarehouseId(id));
    }
    @GetMapping("/{id}/partners/{name}/token")
    @PreAuthorize("hasAuthority('all') or hasAuthority('warehouse_permission')")
    public ApiResponse<String> getDecryptedToken(
            @PathVariable Integer id,
            @PathVariable String name) {
        return new ApiResponse<>(warehouseService.getDecryptedToken(id, name));
    }
    @GetMapping("/internal/{code}")
    public ApiResponse<WarehouseResponse> getByCode(@PathVariable String code){
        return new ApiResponse<>(warehouseService.getByCode(code));
    }

}