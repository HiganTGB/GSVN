package com.gsvn.shipmentservice.client;

import com.gsvn.shipmentservice.common.ApiResponse;
import com.gsvn.shipmentservice.config.InternalFeignConfig;
import com.gsvn.shipmentservice.model.dto.internal.InventoryUpdateRequest;
import com.gsvn.shipmentservice.model.internal.WarehouseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", path = "/api/v1",configuration = InternalFeignConfig.class )
public interface InventoryClient {
    @GetMapping("/warehouses/internal/{code}")
    ApiResponse<WarehouseResponse> getByCode(@PathVariable String code);
    @PostMapping("/inventory/internal/process-ready-to-pick")
    ApiResponse<Void> processReadyToPick(@RequestBody InventoryUpdateRequest request);

    @PostMapping("/inventory/internal/process-packed")
    ApiResponse<Void> processPacked(@RequestBody InventoryUpdateRequest request);
}
