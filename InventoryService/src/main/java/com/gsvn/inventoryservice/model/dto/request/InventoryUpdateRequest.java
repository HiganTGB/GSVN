package com.gsvn.inventoryservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryUpdateRequest {
    private String skuCode;
    private Integer warehouseId;
    private Integer quantity;
}