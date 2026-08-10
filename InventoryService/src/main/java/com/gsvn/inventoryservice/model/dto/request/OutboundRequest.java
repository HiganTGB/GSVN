package com.gsvn.inventoryservice.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboundRequest {

    @NotNull(message = "WAREHOUSE_ID_REQUIRED")
    private Integer warehouseId;

    @NotBlank(message = "TYPE_REQUIRED")
    @Pattern(regexp = "^(TRANSFER|ADJUST)$", message = "OUTBOUND_TYPE_INVALID")
    private String type;

    @Size(max = 100, message = "EXTERNAL_ID_TOO_LONG")
    private String externalId;

    @NotEmpty(message = "ITEMS_REQUIRED")
    @Valid
    private List<OutboundItemRequest> items;

    private String note;
    @Data
    public static class OutboundItemRequest {
        @NotNull(message = "SKU_ID_REQUIRED")
        private Long skuId;

        @NotNull(message = "QUANTITY_REQUIRED")
        @Min(value = 1, message = "QUANTITY_MUST_BE_GREATER_THAN_ZERO")
        private Integer quantity;
    }
}