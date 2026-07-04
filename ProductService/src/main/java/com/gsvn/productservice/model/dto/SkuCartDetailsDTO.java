package com.gsvn.productservice.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class SkuCartDetailsDTO {
    private Long skuId;
    private String skuCode;
    private String productName;
    private Integer productId;
    private String imageUrl;
    private BigDecimal sellingPrice;
    private BigDecimal prePrice;
    private BigDecimal preDepositAmount;
    private Integer prePerQty;

    private OffsetDateTime preStartAt;
    private OffsetDateTime preEndAt;
    private LocalDate preReleaseDate;
    private Boolean isProductActive;
    private Boolean isPreOrder;

    private Boolean isSkuActive;

    private Long physicalAvailable=null;
    private Integer preLimit=null;
    private Integer preOrders=null;
}