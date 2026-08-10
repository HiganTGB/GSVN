package com.gsvn.searchservice.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    public boolean isSellable() {
        boolean hasPhysical = physicalAvailable != null && physicalAvailable > 0;

        boolean hasPreorderSlot = preLimit != null && preLimit > 0 && preOrders < preLimit;

        return hasPhysical || hasPreorderSlot;
    }
}