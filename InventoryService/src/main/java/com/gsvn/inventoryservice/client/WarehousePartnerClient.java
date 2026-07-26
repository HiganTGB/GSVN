package com.gsvn.inventoryservice.client;



import com.gsvn.inventoryservice.config.InternalFeignConfig;
import com.gsvn.inventoryservice.model.internal.WarehousePartnerRequest;
import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.model.internal.WarehousePartnerResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "shipment-service", path = "/api/v1/warehouseconfig/internal",configuration = InternalFeignConfig.class)
public interface WarehousePartnerClient {

    @PostMapping("/{code}/partners")
    ApiResponse<WarehousePartnerResponse> savePartner(
            @PathVariable String code,
            @RequestBody @Valid WarehousePartnerRequest request
    );

    @DeleteMapping("/{code}/partners/{name}")
    ApiResponse<Void> deletePartner(
            @PathVariable String code,
            @PathVariable String name
    );

    @GetMapping("/{code}/partners")
    ApiResponse<List<WarehousePartnerResponse>> getPartners(
            @PathVariable String code
    );

    @GetMapping("/{code}/partners/{name}/token")
    ApiResponse<String> getDecryptedToken(
            @PathVariable String code,
            @PathVariable String name
    );
}