package com.gsvn.productservice.model.dto.request;

import com.gsvn.productservice.model.entity.Dimensions;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class SkuRequest {


    private Long id;

    @NotBlank(message = "SKU_CODE_REQUIRED")
    private String skuCode;

    @NotNull(message = "IMPORT_PRICE_REQUIRED")
    @Min(value = 0, message = "PRICE_INVALID")
    private BigDecimal importPrice;

    @NotNull(message = "SELLING_PRICE_REQUIRED")
    @Min(value = 0, message = "PRICE_INVALID")
    private BigDecimal sellingPrice;


    private BigDecimal prePrice;

    private BigDecimal preDepositAmount;
    @NotNull(message = "LIMIT_REQUIRED")
    @Min(value = 0, message = "LIMIT_INVALID")
    private Integer preLimitQuantity = 0;

    @Min(value = 1, message = "PRE_QTY_MIN_1")
    private Integer prePerQty = 3;

    @Min(value = 0, message = "WEIGHT_INVALID")
    private Integer weightGram = 0;

    @Valid
    private Dimensions dimensions;
    private Boolean isActive;
    private Map<Long,Long> variantOptionIds;
}