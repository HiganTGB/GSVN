package com.gsvn.productservice.mapper;

import com.gsvn.productservice.model.dto.response.VariantResponse;
import com.gsvn.productservice.model.entity.Variant;
import com.gsvn.productservice.model.entity.VariantOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VariantMapper {
    // Variants
    List<Variant> findVariantsByProductId(Integer productId);
    void insertVariant(Variant variant);
    void updateVariant(Variant variant);
    void deleteVariant(Long id);

    // Options
    List<VariantOption> findOptionsByVariantId(Long variantId);
    void insertOption(VariantOption option);
    void updateOption(VariantOption option);
    void deleteOption(Long id);
    void deleteOptionsByVariantId(Long variantId);
    int countVariantsByProductId(Integer productId);
    // DTO for Detail View
    List<VariantResponse> findVariantsWithOptions(Integer productId);
}