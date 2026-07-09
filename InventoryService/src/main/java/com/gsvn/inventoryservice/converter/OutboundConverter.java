package com.gsvn.inventoryservice.converter;

import com.gsvn.inventoryservice.model.dto.request.OutboundRequest;
import com.gsvn.inventoryservice.model.dto.response.OutboundResponse;
import com.gsvn.inventoryservice.model.entity.OutboundItem;
import com.gsvn.inventoryservice.model.entity.OutboundReceipt;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OutboundConverter {

    public OutboundReceipt toEntity(OutboundRequest request) {
        if (request == null) return null;

        return OutboundReceipt.builder()
                .warehouseId(request.getWarehouseId())
                .type(request.getType())
                .externalId(request.getExternalId())
                .note(request.getNote())
                .build();
    }

    public OutboundResponse toResponse(OutboundReceipt entity, List<OutboundItem> items) {
        if (entity == null) return null;

        return OutboundResponse.builder()
                .id(entity.getId())
                .warehouseId(entity.getWarehouseId())
                .receiptCode(entity.getReceiptCode())
                .type(entity.getType())
                .externalId(entity.getExternalId())
                .staffId(entity.getStaffId())
                .createdAt(entity.getCreatedAt())
                .items(toItemResponseList(items))
                .build();
    }

    private List<OutboundResponse.OutboundItemDetail> toItemResponseList(List<OutboundItem> items) {
        if (items == null) return Collections.emptyList();
        return items.stream()
                .map(item -> OutboundResponse.OutboundItemDetail.builder()
                        .skuId(item.getSkuId())
                        .quantity(item.getQuantity())
                        .skuCode(item.getSkuCode())
                        .productName(item.getProductName())
                        .build())
                .collect(Collectors.toList());
    }

    public List<OutboundResponse> toResponseList(List<OutboundReceipt> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(entity -> toResponse(entity, null))
                .collect(Collectors.toList());
    }
}