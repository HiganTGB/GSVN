package com.tgb.gsvnbackend.model.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemDomain(String skuId,Integer quantity) {

}
