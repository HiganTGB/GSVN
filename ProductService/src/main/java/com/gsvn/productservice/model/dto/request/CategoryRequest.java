package com.gsvn.productservice.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "CATEGORY_NAME_REQUIRED")
    @Size(max = 255, message = "CATEGORY_NAME_TOO_LONG")
    private String name;

    @Size(max = 1000, message = "CATEGORY_DESCRIPTION_TOO_LONG")
    private String description;

    private Integer parentCategoryId;
}