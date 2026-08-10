package com.gsvn.productservice.model.internal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkuGlobalSyncRequest {

    @NotNull(message = "SKU_ID_REQUIRED")
    private Long skuId;
    @NotNull(message = "SKU_CODE_REQUIRED")
    private String skuCode;

    @Min(value = 0, message = "LIMIT_QUANTITY_INVALID")
    private Integer preLimitQuantity;
}