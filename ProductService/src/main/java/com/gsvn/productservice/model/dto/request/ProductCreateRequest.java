package com.gsvn.productservice.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductCreateRequest {
    @NotBlank(message = "PRODUCT_NAME_REQUIRED")
    @Size(max = 500, message = "PRODUCT_NAME_TOO_LONG")
    private String name;

    @NotNull(message = "CATEGORY_ID_REQUIRED")
    private Integer categoryId;

    @NotNull(message = "BRAND_ID_REQUIRED")
    private Integer brandId;

    private String description;

    @Size(max = 50, message = "RELEASE_DATE_TOO_LONG")
    private String releaseDate = "TBA";
}