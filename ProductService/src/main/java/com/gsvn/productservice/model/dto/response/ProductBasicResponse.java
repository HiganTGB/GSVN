package com.gsvn.productservice.model.dto.response;

import com.gsvn.productservice.model.entity.SaleStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ProductBasicResponse {
    private Integer id;
    private String name;
    private Integer categoryId;
    private Integer brandId;
    private String description;
    private String releaseDate;
    private String imageUrl;
    private SaleStatus saleStatus;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
