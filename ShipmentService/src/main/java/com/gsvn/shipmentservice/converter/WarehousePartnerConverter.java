package com.gsvn.shipmentservice.converter;

import com.gsvn.shipmentservice.model.dto.request.WarehousePartnerRequest;
import com.gsvn.shipmentservice.model.dto.response.WarehousePartnerResponse;
import com.gsvn.shipmentservice.model.entity.WarehousePartner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WarehousePartnerConverter {

    public WarehousePartner toEntity(WarehousePartnerRequest request, String warehouseCode) {
        if (request == null) return null;
        return WarehousePartner.builder()
                .warehouseCode(warehouseCode)
                .partnerName(request.getPartnerName())
                .shopId(request.getShopId())
                .expiresAt(OffsetDateTime.from(request.getExpiresAt()))
                .build();
    }

    public WarehousePartnerResponse toResponse(WarehousePartner entity) {
        if (entity == null) return null;
        return WarehousePartnerResponse.builder()
                .id(entity.getId())
                .warehouseCode(entity.getWarehouseCode())
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