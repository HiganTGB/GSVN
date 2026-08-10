package com.gsvn.productservice.service.impl;

import com.gsvn.productservice.exc.AppException;
import com.gsvn.productservice.exc.ErrorCode;
import com.gsvn.productservice.mapper.SkuMapper;
import com.gsvn.productservice.mapper.VariantMapper;
import com.gsvn.productservice.model.dto.request.ProductVariantSyncRequest;
import com.gsvn.productservice.model.dto.response.VariantResponse;
import com.gsvn.productservice.model.entity.Variant;
import com.gsvn.productservice.model.entity.VariantOption;
import com.gsvn.productservice.service.VariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class VariantServiceImpl implements VariantService {
    private final VariantMapper variantMapper;
    private final SkuMapper skuMapper;

    @Transactional
    public void syncVariants(Integer productId, List<ProductVariantSyncRequest.VariantUpdateDto> requestVariants) {
        List<Variant> currentVariants = variantMapper.findVariantsByProductId(productId);
        long skuCount = skuMapper.countByProductId(productId);
        List<Long> requestVariantIds = requestVariants.stream()
                .map(ProductVariantSyncRequest.VariantUpdateDto::getId)
                .filter(Objects::nonNull)
                .toList();

        for (Variant current : currentVariants) {
            if (!requestVariantIds.contains(current.getId())) {
                if (skuCount > 0) {
                    throw new AppException(ErrorCode.SKU_EXISTED);
                }
                variantMapper.deleteOptionsByVariantId(current.getId());
                variantMapper.deleteVariant(current.getId());
            }
        }

        for (var vDto : requestVariants) {
            boolean isNewVariant = (vDto.getId() == null);
            Variant variantEntity = new Variant();
            variantEntity.setProductId(productId);
            variantEntity.setName(vDto.getName());

            if (isNewVariant) {

                validateNewVariantWithSku(skuCount, vDto);
                variantMapper.insertVariant(variantEntity);
                vDto.setId(variantEntity.getId());
            } else {

                variantEntity.setId(vDto.getId());
                variantMapper.updateVariant(variantEntity);
            }

            Long newOptionId = syncOptions(vDto.getId(), vDto.getOptions(), skuCount, isNewVariant);

            if (isNewVariant && skuCount > 0) {
                autoSyncNewOptionToExistingSkus(productId, newOptionId);
            }
        }
    }

    private Long syncOptions(Long variantId, List<ProductVariantSyncRequest.OptionUpdateDto> requestOptions, long skuCount, boolean isNewVariant) {
        List<VariantOption> currentOptions = variantMapper.findOptionsByVariantId(variantId);
        List<Long> requestOptionIds = requestOptions.stream()
                .map(ProductVariantSyncRequest.OptionUpdateDto::getId)
                .filter(Objects::nonNull)
                .toList();

        for (VariantOption currentOpt : currentOptions) {
            if (!requestOptionIds.contains(currentOpt.getId())) {

                if (skuCount > 0) {
                    throw new AppException(ErrorCode.SKU_EXISTED);
                }
                variantMapper.deleteOption(currentOpt.getId());
            }
        }

        Long lastInsertedId = null;

        for (var oDto : requestOptions) {
            VariantOption optionEntity = new VariantOption();
            optionEntity.setVariantId(variantId);
            optionEntity.setName(oDto.getName());

            if (oDto.getId() == null) {

                variantMapper.insertOption(optionEntity);
                lastInsertedId = optionEntity.getId();
            } else {

                optionEntity.setId(oDto.getId());
                variantMapper.updateOption(optionEntity);
            }
        }
        return lastInsertedId;
    }

    private void autoSyncNewOptionToExistingSkus(Integer productId, Long newOptionId) {

        List<Long> skuIds = skuMapper.findAllIdsByProductId(productId);
        if (!skuIds.isEmpty() && newOptionId != null) {
            for (var skuId : skuIds) {

                skuMapper.insertSingleVariantMap(skuId, newOptionId);
            }
            log.info("Auto-mapped new option {} to existing {} SKUs", newOptionId, skuIds.size());
        }
    }
    private void validateNewVariantWithSku(long skuCount, ProductVariantSyncRequest.VariantUpdateDto vDto) {

        if (skuCount > 0) {
            if (vDto.getOptions() == null || vDto.getOptions().size() != 1) {
                throw new AppException(ErrorCode.LIMIT_ONE_OPTION);
            }
        }
    }
    public List<VariantResponse> getVariantByProduct(Integer productId)
    {
        return variantMapper.findVariantsWithOptions(productId);
    }


}