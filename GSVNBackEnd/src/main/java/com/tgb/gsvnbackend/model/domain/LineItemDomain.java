package com.tgb.gsvnbackend.model.domain;


import java.math.BigDecimal;


public record LineItemDomain(String id,
                             String sku,
                             String name,
                             Integer quantity,
                             BigDecimal retail,
                             BigDecimal sale) {
}
