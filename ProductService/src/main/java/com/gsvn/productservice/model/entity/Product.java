package com.gsvn.productservice.model.entity;

import com.gsvn.productservice.model.entity.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Integer id;
    private String name;
    private Integer categoryId;
    private Integer brandId;
    private String description;
    private String releaseDate;
    private String imageUrl;

    // Map từ JSONB sang List<String>
    private List<String> galleryImages;

    private SaleStatus saleStatus;
    private OffsetDateTime deletedAt;
    private Boolean isActive;

    // Pre-order settings
    private String preName;
    private Boolean preIsActive;
    private OffsetDateTime preStartAt;
    private OffsetDateTime preEndAt;
    private LocalDate preReleaseDate;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    public Boolean canChangeActiveCampaign()
    {
        return !preName.isBlank() && preEndAt != null && preStartAt != null;
    }


}