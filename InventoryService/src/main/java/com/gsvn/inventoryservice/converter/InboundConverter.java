package com.gsvn.inventoryservice.converter;

import com.gsvn.inventoryservice.model.dto.request.InboundRequest;
import com.gsvn.inventoryservice.model.dto.response.InboundResponse;
import com.gsvn.inventoryservice.model.entity.InboundItem;
import com.gsvn.inventoryservice.model.entity.InboundReceipt;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InboundConverter {

    public InboundReceipt toEntity(InboundRequest request) {
        if (request == null) return null;

        return InboundReceipt.builder()
                .warehouseId(request.getWarehouseId())
                .sourceOutboundCode(request.getSourceOutboundCode())
                .supplierId(request.getSupplierId())
                .type(request.getType())
                .note(request.getNote())
                .build();
    }

    public InboundResponse toResponse(InboundReceipt entity, List<InboundItem> items) {
        if (entity == null) return null;

        return InboundResponse.builder()
                .id(entity.getId())
                .warehouseId(entity.getWarehouseId())
                .receiptCode(entity.getReceiptCode())
                .sourceOutboundCode(entity.getSourceOutboundCode())
                .supplierId(entity.getSupplierId())
                .type(entity.getType())
                .staffId(entity.getStaffId())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .items(toItemResponseList(items))
                .build();
    }

    private List<InboundResponse.InboundItemDetail> toItemResponseList(List<InboundItem> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();

        return items.stream()
                .map(this::toItemDetail)
                .collect(Collectors.toList());
    }

    private InboundResponse.InboundItemDetail toItemDetail(InboundItem item) {
        return InboundResponse.InboundItemDetail.builder()
                .skuId(item.getSkuId())
                .skuCode(item.getSkuCode())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .importPrice(item.getImportPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }

    public List<InboundResponse> toResponseList(List<InboundReceipt> entities) {
        if (entities == null) return Collections.emptyList(); // Trả về list trống thay vì null để an toàn
        return entities.stream()
                .map(entity -> toResponse(entity, null))
                .collect(Collectors.toList());
    }
}