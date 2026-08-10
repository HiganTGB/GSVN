package com.gsvn.inventoryservice.converter;

import com.gsvn.inventoryservice.model.dto.response.SkuStockResponse;
import com.gsvn.inventoryservice.model.entity.SkuStock;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SkuStockConverter {

    public SkuStockResponse toResponse(SkuStock entity) {
        if (entity == null) return null;

        SkuStockResponse response = new SkuStockResponse();
        response.setSkuId(entity.getSkuId());
        response.setSkuCode(entity.getSkuCode());
        response.setWarehouseId(entity.getWarehouseId());
        response.setPhysicalStock(entity.getPhysicalStock());
        response.setReservedStock(entity.getReservedStock());
        response.setVersion(entity.getVersion());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setAvailableStock(entity.getAvailableStock());
        return response;
    }

    public List<SkuStockResponse> toResponseList(List<SkuStock> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}