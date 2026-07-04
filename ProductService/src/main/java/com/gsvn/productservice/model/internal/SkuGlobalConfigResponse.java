package com.gsvn.productservice.model.internal;

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

    private Integer skuId;

    private Integer preLimitQuantity;

    private Integer preCurrentOrders;

    private Integer availablePreOrderQty;

    private Integer reservedGlobal;

    private OffsetDateTime updatedAt;

}