package com.gsvn.productservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkuVariantMapDto {
    private Long skuId;
    private Integer optionId;
}