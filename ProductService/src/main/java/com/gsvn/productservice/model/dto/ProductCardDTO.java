package com.gsvn.productservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gsvn.productservice.model.entity.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCardDTO {
    private Integer id;
    private String name;
    private String imageUrl;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private SaleStatus saleStatus;
    private Boolean isActive;
    private String preName;
    private Boolean preIsActive;
    private Integer brandId;
    private String brandName;
    private Integer categoryId;
    private String categoryName;
    private List<Long> skuIds;
    private Boolean isOutOfStock;
    private String displayStatus;
    @JsonIgnore
    private OffsetDateTime createdAt;
}