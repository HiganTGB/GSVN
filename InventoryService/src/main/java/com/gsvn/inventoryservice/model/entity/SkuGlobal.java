package com.gsvn.inventoryservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuGlobal {
    private Long skuId;
    private String skuCode;
    private Integer preLimitQuantity;
    private Integer preCurrentOrders;
    private Integer reservedGlobal;
    private Integer version;
    private OffsetDateTime updatedAt;
}