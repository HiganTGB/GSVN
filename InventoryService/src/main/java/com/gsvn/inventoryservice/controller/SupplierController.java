package com.gsvn.inventoryservice.controller;


import com.gsvn.inventoryservice.model.dto.request.SupplierRequest;
import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.response.SupplierResponse;
import com.gsvn.inventoryservice.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ApiResponse<SupplierResponse> create(@RequestBody @Valid SupplierRequest request) {
        return new ApiResponse<>(supplierService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<SupplierResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid SupplierRequest request) {
        return new ApiResponse<>(supplierService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<SupplierResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(supplierService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        supplierService.delete(id);
        return new ApiResponse<>();
    }

    @GetMapping
    public ApiResponse<PageResponse<SupplierResponse>> getPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return new ApiResponse<>(supplierService.getPage(keyword, isActive,sortBy,direction, page, size));
    }

    @GetMapping("/all")
    public ApiResponse<List<SupplierResponse>> getAll() {
        return new ApiResponse<>(supplierService.getAll());
    }
}