package com.gsvn.productservice.model.dto.response;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class ProductSummaryResponse {
    private Integer id;
    private String name;
    private String brandName;
    private String categoryName;
    private String saleStatus;
    private String imageUrl;
    private Integer skuCount;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}