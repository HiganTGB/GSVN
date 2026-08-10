package com.gsvn.productservice.converter;

import com.gsvn.productservice.model.dto.request.BrandRequest;
import com.gsvn.productservice.model.dto.response.BrandResponse;
import com.gsvn.productservice.model.entity.Brand;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BrandConverter {


    public Brand toEntity(BrandRequest request) {
        if (request == null) return null;
        Brand entity = new Brand();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return entity;
    }


    public BrandResponse toResponse(Brand entity) {
        if (entity == null) return null;
        return BrandResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<BrandResponse> toResponseList(List<Brand> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }


    public void updateEntity(BrandRequest request, Brand entity) {
        if (request == null || entity == null) return;
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
    }
}