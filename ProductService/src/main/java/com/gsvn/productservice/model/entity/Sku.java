package com.gsvn.productservice.model.entity;

import com.gsvn.productservice.model.entity.Dimensions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sku {
    private Long id;
    private Integer productId;
    private String skuCode;
    private BigDecimal importPrice;
    private BigDecimal sellingPrice;

    // Pre-order Data
    private BigDecimal prePrice;
    private BigDecimal preDepositAmount;
    private Integer prePerQty;

    private Integer weightGram;

    // Map từ JSONB sang class Dimensions
    private Dimensions dimensionsCm;

    private Boolean isActive;
    private OffsetDateTime deletedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}