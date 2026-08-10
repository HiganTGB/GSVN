package com.gsvn.cartservice.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
    @NotNull(message = "SKU_ID_REQUIRED")
    private Long skuId;

    @NotNull(message = "QUANTITY_REQUIRED")
    @Min(value = 1, message = "QUANTITY_MIN_1")
    private Integer quantity;

    private Boolean isDeposit = false; // Mặc định là mua thẳng nếu không truyền
}