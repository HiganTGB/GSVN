package com.gsvn.inventoryservice.model.entity;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboundItem {
    private Long id;
    private Long inboundId;
    private Long skuId;
    private String skuCode;
    private String productName;
    private Integer quantity;
    private BigDecimal importPrice;
    private BigDecimal lineTotal;
}