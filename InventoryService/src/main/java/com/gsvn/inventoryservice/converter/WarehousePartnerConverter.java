package com.gsvn.inventoryservice.converter;

import com.gsvn.inventoryservice.model.internal.WarehousePartnerRequest;
import com.gsvn.inventoryservice.model.internal.WarehousePartnerResponse;
import com.gsvn.inventoryservice.model.entity.WarehousePartner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WarehousePartnerConverter {

    public WarehousePartner toEntity(WarehousePartnerRequest request, Integer warehouseId) {
        if (request == null) return null;
        return WarehousePartner.builder()
                .warehouseId(warehouseId)
                .partnerName(request.getPartnerName())
                .shopId(request.getShopId())
                .expiresAt(OffsetDateTime.from(request.getExpiresAt()))
                .build();
    }

    public WarehousePartnerResponse toResponse(WarehousePartner entity) {
        if (entity == null) return null;
        return WarehousePartnerResponse.builder()
                .id(entity.getId())
                .warehouseCode(entity.getWarehouseId())
                .partnerName(entity.getPartnerName())
                .shopId(entity.getShopId())
                .partnerToken(entity.getPartnerToken())
                .expiresAt(entity.getExpiresAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    public List<WarehousePartnerResponse> toResponseList(List<WarehousePartner> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}