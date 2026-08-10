package com.gsvn.inventoryservice.queue.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequestMessage {
    private String orderCode;
    private String sagaId;
    private String warehouseCode;
    private String deliveryMethod;
    private List<InventoryItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryItem {
        private String skuCode;
        private Integer quantity;
        private Boolean isPreorder;
    }
}