package com.gsvn.searchservice.model.response;

import com.gsvn.searchservice.model.internal.SkuSellableDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuInfoResponse {
    private Long id;
    private String skuCode;
    private BigDecimal sellingPrice;
    private BigDecimal prePrice;
    private BigDecimal preDepositAmount;
    private Boolean isActive;

    private Integer currentWarehouseStock;

    private Integer otherWarehousesStock;

    private SkuSellableDTO inventoryStatus;
}