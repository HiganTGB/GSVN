package com.gsvn.shipmentservice.controller;

import com.gsvn.shipmentservice.common.ApiResponse;
import com.gsvn.shipmentservice.model.dto.request.WarehousePartnerRequest;
import com.gsvn.shipmentservice.model.dto.response.WarehousePartnerResponse;
import com.gsvn.shipmentservice.service.WarehousePartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouseconfig/internal")
@RequiredArgsConstructor
public class WarehousePartnerController {

    private final WarehousePartnerService warehouseService;

    @PostMapping("/{code}/partners")
    public ApiResponse<WarehousePartnerResponse> savePartner(
            @PathVariable String code,
            @RequestBody @Valid WarehousePartnerRequest request
    ) {
        return new ApiResponse<>(warehouseService.savePartnerToken(request,code ));
    }

    @DeleteMapping("/{code}/partners/{name}")
    public ApiResponse<Void> deletePartner(
            @PathVariable String code,
            @PathVariable String name
    ) {
        warehouseService.deletePartner(code, name);
        return new ApiResponse<>();
    }
    @GetMapping("/{code}/partners")
    public ApiResponse<List<WarehousePartnerResponse>> getPartners( @PathVariable String code) {
        return new ApiResponse<>(warehouseService.getPartnersByWarehouseId(code));
    }
    @GetMapping("/{code}/partners/{name}/token")
    public ApiResponse<String> getDecryptedToken(
            @PathVariable String code,
            @PathVariable String name) {
        return new ApiResponse<>(warehouseService.getDecryptedToken(code, name));
    }
}