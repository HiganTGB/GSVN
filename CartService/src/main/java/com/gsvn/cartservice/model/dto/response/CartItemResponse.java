package com.gsvn.cartservice.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Integer id;
    private Long skuId;
    private String skuCode;
    private Integer quantity;
    private Boolean isDeposit;
    private Integer productId;
    private String productName;
    private String imageUrl;

    private BigDecimal originalPrice;
    private BigDecimal prePrice;
    private BigDecimal depositAmount;

    private BigDecimal subOriginalTotal;
    private BigDecimal subPreTotal;
    private BigDecimal subDepositTotal;

    private Boolean isPreOrder;
    private OffsetDateTime preStartAt;
    private OffsetDateTime preEndAt;
    @JsonFormat(pattern = "MM/yyyy")
    private LocalDate preReleaseDate;

    private Boolean isAvailable;
    private Long maxAvailable;
}