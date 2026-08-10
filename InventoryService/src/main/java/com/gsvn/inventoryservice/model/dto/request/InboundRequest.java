package com.gsvn.inventoryservice.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboundRequest {

    @NotNull(message = "WAREHOUSE_ID_REQUIRED")
    private Integer warehouseId;

    @Size(max = 50, message = "SOURCE_CODE_TOO_LONG")
    private String sourceOutboundCode;

    private Integer supplierId;

    @NotBlank(message = "TYPE_REQUIRED")
    @Pattern(regexp = "^(PURCHASE|TRANSFER|ADJUST)$", message = "INBOUND_TYPE_INVALID")
    private String type;

    private String note;

    @NotEmpty(message = "ITEMS_REQUIRED")
    @Valid
    private List<InboundItemRequest> items;

    @Data
    public static class InboundItemRequest {
        @NotNull(message = "SKU_ID_REQUIRED")
        private Long skuId;

        @NotNull(message = "QUANTITY_REQUIRED")
        @Min(value = 1, message = "QUANTITY_MUST_BE_GREATER_THAN_ZERO")
        private Integer quantity;

        @NotNull(message = "IMPORT_PRICE_REQUIRED")
        @DecimalMin(value = "0.0", message = "IMPORT_PRICE_INVALID")
        private BigDecimal importPrice;
    }
}