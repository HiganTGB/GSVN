package com.gsvn.productservice.converter;

import com.gsvn.productservice.model.dto.request.CategoryRequest;
import com.gsvn.productservice.model.dto.response.CategoryResponse;
import com.gsvn.productservice.model.entity.Category;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryConverter {

    public Category toEntity(CategoryRequest request) {
        if (request == null) return null;
        Category entity = new Category();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setParentCategoryId(request.getParentCategoryId());
        return entity;
    }

    public CategoryResponse toResponse(Category entity) {
        if (entity == null) return null;

        CategoryResponse response = CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .parentCategoryId(entity.getParentCategoryId())
                .build();


        if (entity.getSubCategories() != null && !entity.getSubCategories().isEmpty()) {
            response.setChildren(entity.getSubCategories().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList()));
        } else {
            response.setChildren(new ArrayList<>());
        }

        return response;
    }

    public List<CategoryResponse> toResponseList(List<Category> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }
}