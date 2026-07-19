package com.gsvn.orderservice.model.entity;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long id;
    private Long orderId;

    private String skuCode;
    private String productName;
    private String imageUrl;
    private LocalDate scheduledDate;

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subPrice;


    private Boolean isPreorder;
    private Boolean isDepositApplied;
    private BigDecimal appliedDepositAmount;

    private OffsetDateTime createdAt;
}