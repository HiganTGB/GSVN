package com.gsvn.productservice.converter;

import com.gsvn.productservice.model.dto.request.SkuRequest;
import com.gsvn.productservice.model.dto.response.SkuResponse;
import com.gsvn.productservice.model.entity.Sku;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkuConverter {

    public Sku toEntity(SkuRequest request, Integer productId) {
        if (request == null) return null;

        Sku entity = new Sku();
        entity.setProductId(productId);
        entity.setSkuCode(request.getSkuCode());
        entity.setImportPrice(request.getImportPrice());
        entity.setSellingPrice(request.getSellingPrice());
        entity.setPrePrice(request.getPrePrice());
        entity.setPreDepositAmount(request.getPreDepositAmount());
        entity.setPrePerQty(request.getPrePerQty());
        entity.setWeightGram(request.getWeightGram());
        entity.setIsActive(request.getIsActive());
        entity.setDimensionsCm(request.getDimensions());
        return entity;
    }
    public void updateEntity(SkuRequest request, Sku entity) {
        if (request == null || entity == null) return;

        entity.setSkuCode(request.getSkuCode());
        entity.setImportPrice(request.getImportPrice());
        entity.setSellingPrice(request.getSellingPrice());
        entity.setPrePrice(request.getPrePrice());
        entity.setPreDepositAmount(request.getPreDepositAmount());
        entity.setPrePerQty(request.getPrePerQty());
        entity.setWeightGram(request.getWeightGram());
        entity.setDimensionsCm(request.getDimensions());
        entity.setIsActive(request.getIsActive());
    }
    public SkuResponse toResponse(Sku entity, List<Integer> optionIds) {
        if (entity == null) return null;

        return SkuResponse.builder()
                .id(entity.getId())
                .skuCode(entity.getSkuCode())
                .importPrice(entity.getImportPrice())
                .sellingPrice(entity.getSellingPrice())
                .prePrice(entity.getPrePrice())
                .preDepositAmount(entity.getPreDepositAmount())
                .prePerQty(entity.getPrePerQty())
                .weightGram(entity.getWeightGram())
                .dimensionsCm(entity.getDimensionsCm())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .optionIds(optionIds)
                .build();
    }
}