package com.gsvn.inventoryservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuStock {
    private Long skuId;
    private String skuCode;
    private Integer warehouseId;
    private Integer physicalStock;

    private Integer reservedStock;

    private Integer version;

    private OffsetDateTime updatedAt;

    public int getAvailableStock() {
        return this.physicalStock - this.reservedStock;
    }
}
