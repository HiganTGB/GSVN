package com.gsvn.productservice.model.dto;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class SkuSearchResponse {
    private Long skuId;
    private String skuCode;
    private String productName;
    private String imageUrl;
    private BigDecimal currentImportPrice;
    private BigDecimal sellingPrice;
}