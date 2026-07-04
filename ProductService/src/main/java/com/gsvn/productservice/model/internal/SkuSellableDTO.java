package com.gsvn.productservice.model.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuSellableDTO {

    private Long skuId;

    private Long physicalAvailable;

    private Integer preLimit;

    private Integer preOrders;
}