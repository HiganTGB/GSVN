package com.gsvn.inventoryservice.model.internal;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuSearchResponse {
    private Long skuId;
    private String productName;
    private String skuCode;
    private String imageUrl;
    private BigDecimal currentImportPrice;
    private BigDecimal sellingPrice;
}