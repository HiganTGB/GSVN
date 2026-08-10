package com.gsvn.productservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private Integer id;
    private String name;
    private String description;
    private Integer parentCategoryId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private Category parentCategory;
    private List<Category> subCategories;
}