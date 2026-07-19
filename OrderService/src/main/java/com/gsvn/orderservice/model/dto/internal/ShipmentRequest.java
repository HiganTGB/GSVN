package com.gsvn.orderservice.model.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentRequest {
    private Long orderId;
    private String orderCode;
    private String warehouseCode;
    private String deliveryMethod;
    private LocalDate scheduledDate;

    private BigDecimal totalCodAmount;


    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private String provinceCode;
    private String wardCode;
    private String addressDetail;
    private String customerNote;

    private List<ItemRequest> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemRequest {
        private Long orderItemId;
        private String skuCode;
        private Integer quantity;
        private String productName;
    }
}