package com.gsvn.inventoryservice.converter;

import com.gsvn.inventoryservice.model.dto.request.SkuGlobalSyncRequest;
import com.gsvn.inventoryservice.model.dto.response.SkuGlobalConfigResponse;
import com.gsvn.inventoryservice.model.entity.SkuGlobal;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SkuGlobalConverter {
    public SkuGlobal toEntity(SkuGlobalSyncRequest request) {
        if (request == null) return null;

        return SkuGlobal.builder()
                .skuId(request.getSkuId())
                .skuCode(request.getSkuCode())
                .preLimitQuantity(request.getPreLimitQuantity() != null ? request.getPreLimitQuantity() : 0)
                .build();
    }
    public SkuGlobalConfigResponse toResponse(SkuGlobal entity) {
        if (entity == null) return null;

        int preLimit = entity.getPreLimitQuantity() != null ? entity.getPreLimitQuantity() : 0;
        int currentOrders = entity.getPreCurrentOrders() != null ? entity.getPreCurrentOrders() : 0;

        int available = (preLimit == 0) ? 0 : Math.max(0, preLimit - currentOrders);

        return SkuGlobalConfigResponse.builder()
                .skuId(entity.getSkuId())
                .preLimitQuantity(preLimit)
                .preCurrentOrders(currentOrders)
                .availablePreOrderQty(available)
                .reservedGlobal(entity.getReservedGlobal())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<SkuGlobalConfigResponse> toResponseList(List<SkuGlobal> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntity(SkuGlobal entity, SkuGlobalSyncRequest request) {
        if (request == null || entity == null) return;


        if (request.getPreLimitQuantity() != null) {
            entity.setPreLimitQuantity(request.getPreLimitQuantity());
        }
    }
}