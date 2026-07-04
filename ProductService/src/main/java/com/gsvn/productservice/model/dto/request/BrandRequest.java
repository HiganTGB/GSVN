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
public class BrandRequest {

    @NotBlank(message = "BRAND_NAME_REQUIRED")
    @Size(max = 500, message = "BRAND_NAME_TOO_LONG")
    private String name;

    @Size(max = 2000, message = "BRAND_DESCRIPTION_TOO_LONG")
    private String description;

}