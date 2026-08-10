package com.gsvn.inventoryservice.model.entity;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboundItem {
    private Long id;
    private Long outboundId;
    private Long skuId;
    private String skuCode;
    private String productName;
    private Integer quantity;
}