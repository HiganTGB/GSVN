package com.gsvn.inventoryservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuGlobalConfigResponse {

    private Long skuId;

    private Integer preLimitQuantity;

    private Integer preCurrentOrders;

    private Integer availablePreOrderQty;

    private Integer reservedGlobal;

    private OffsetDateTime updatedAt;


}