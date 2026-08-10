package com.gsvn.shipmentservice.model.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryUpdateRequest {
    private String skuCode;
    private Integer warehouseId;
    private Integer quantity;
}