package com.gsvn.inventoryservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryDTO {
    private Long skuId;
    private Integer physicalStock;
    private Integer reservedStock;
    private Integer availableStock;
    private Integer preLimitQuantity;
    private Integer preCurrentOrders;
    private Integer reservedGlobal;
    private OffsetDateTime updatedAt;
    private String productName;
    private String skuCode;
    private String imageUrl;
}
