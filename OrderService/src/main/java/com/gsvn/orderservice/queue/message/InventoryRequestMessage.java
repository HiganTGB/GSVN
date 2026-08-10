package com.gsvn.orderservice.queue.message;

import lombok.*;
import java.util.List;

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