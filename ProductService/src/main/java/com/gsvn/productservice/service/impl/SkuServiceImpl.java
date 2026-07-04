package com.gsvn.productservice.service.impl;

import com.gsvn.productservice.client.InventorySkuClient;
import com.gsvn.productservice.client.MediaClient;
import com.gsvn.productservice.converter.SkuConverter;
import com.gsvn.productservice.exc.AppException;
import com.gsvn.productservice.exc.DuplicateResourceException;
import com.gsvn.productservice.exc.ErrorCode;
import com.gsvn.productservice.mapper.ProductMapper;
import com.gsvn.productservice.mapper.SkuMapper;

import com.gsvn.productservice.mapper.VariantMapper;
import com.gsvn.productservice.model.dto.SkuCartDetailsDTO;
import com.gsvn.productservice.model.dto.SkuOptionMapping;
import com.gsvn.productservice.model.entity.Variant;
import com.gsvn.productservice.model.internal.SkuGlobalConfigResponse;
import com.gsvn.productservice.model.internal.SkuGlobalSyncRequest;
import com.gsvn.productservice.model.dto.request.SkuRequest;

import com.gsvn.productservice.model.dto.response.SkuResponse;
import com.gsvn.productservice.model.entity.Sku;
import com.gsvn.productservice.model.internal.SkuSellableDTO;
import com.gsvn.productservice.service.SkuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkuServiceImpl implements SkuService {

    private final SkuMapper skuMapper;
    private final ProductMapper productMapper;
    private final SkuConverter skuConverter;
    private final InventorySkuClient inventorySkuClient;
    private final VariantMapper variantMapper;
    private final MediaClient mediaClient;

    @Transactional
    public SkuResponse createSku(Integer productId, SkuRequest request) {
        // check existed first
        var product= productMapper.findById(productId).orElseThrow(()->new AppException(ErrorCode.ITEM_NOT_EXISTED));
        if( skuMapper.existedBySkuCodeAndProduct(null, request.getSkuCode(),productId)!=0)
        {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY,"skuCode");
        }
        if(skuMapper.countByProductId(productId)>0&&variantMapper.countVariantsByProductId(productId)==0)
        {
            throw new AppException(ErrorCode.LIMIT_ONE_SKU);
        }
        validateDuplicateOptions(productId, null, request.getVariantOptionIds().values());
        // check valid options (not same variant, must contain all variant)
        List<Long> requiredVariantIds = variantMapper.findVariantsByProductId(productId).stream().map(Variant::getId).toList();
        List<Long> submittedVariantIds = request.getVariantOptionIds().keySet().stream().toList();
        Set<Long> uniqueVariantIds = new HashSet<>(submittedVariantIds);
        if (uniqueVariantIds.size() < submittedVariantIds.size()) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY,"optionIds");
        }
        if (!uniqueVariantIds.containsAll(requiredVariantIds) || uniqueVariantIds.size() != requiredVariantIds.size()) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY,"optionIds");
        }
        // insert first, then add variantmap for sku
        Sku sku = skuConverter.toEntity(request, productId);
        skuMapper.insert(sku);
        request.getVariantOptionIds().values().forEach(x->
                skuMapper.insertSingleVariantMap(sku.getId(),x)
        );
        // call to sync limit preorder
        inventorySkuClient.syncConfig(new SkuGlobalSyncRequest(sku.getId(),sku.getSkuCode(),request.getPreLimitQuantity()));
        List<Integer> optionIds=skuMapper.findOptionIdsBySkuId(sku.getId());
        return skuConverter.toResponse(sku,optionIds);
    }

    @Transactional
    public SkuResponse updateSku( Long skuId, SkuRequest request) {
        // check existed first
        Sku sku = skuMapper.findById(skuId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
        if( skuMapper.existedBySkuCodeAndProduct(skuId, request.getSkuCode(),sku.getProductId())!=0)
        {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY,"skuCode");
        }
        validateDuplicateOptions(sku.getProductId(), sku.getId(), request.getVariantOptionIds().values());
        // check valid options (not same variant, must contain all variant)
        List<Long> requiredVariantIds = variantMapper.findVariantsByProductId(sku.getProductId()).stream().map(Variant::getId).toList();
        List<Long> submittedVariantIds = request.getVariantOptionIds().keySet().stream().toList();
        Set<Long> uniqueVariantIds = new HashSet<>(submittedVariantIds);
        log.error(String.valueOf(requiredVariantIds.size()));
        log.error(submittedVariantIds.toString());
        log.error(String.valueOf(submittedVariantIds.size()));
        log.error(String.valueOf(uniqueVariantIds.size()));
        if (uniqueVariantIds.size() < submittedVariantIds.size()) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY,"optionIds");
        }
        if (!uniqueVariantIds.containsAll(requiredVariantIds) || uniqueVariantIds.size() != requiredVariantIds.size()) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY,"optionIds");
        }
        // remove all variantmap first , then insert new map
        skuMapper.deleteVariantMapBySkuId(skuId);
        request.getVariantOptionIds().values().forEach(x->
                skuMapper.insertSingleVariantMap(sku.getId(),x)
        );
        // update sku
        skuConverter.updateEntity(request, sku);

        int result = skuMapper.updateSkuInfo(sku);
        if (result == 0) {
            throw new AppException(ErrorCode.CONFLICT_VERSION);
        }
        // call to sync limit preorder
        inventorySkuClient.syncConfig(new SkuGlobalSyncRequest(sku.getId(),sku.getSkuCode(),request.getPreLimitQuantity()));
        List<Integer> optionIds=skuMapper.findOptionIdsBySkuId(sku.getId());
        return skuConverter.toResponse(sku,optionIds);
    }
    @Transactional(readOnly = true)
    public List<SkuResponse> getSkusByProduct(Integer productId) {
        if (!productMapper.existsById(productId)) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
        List<Sku> skus = skuMapper.findByProductId(productId);
        if (skus.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> skuIds = skus.stream().map(Sku::getId).toList();
        Map<Long, SkuGlobalConfigResponse> inventoryMap = inventorySkuClient.getBulkStatus(skuIds).result();

        return skus.stream()
                .map(sku -> {
                    List<Integer> optionIds=skuMapper.findOptionIdsBySkuId(sku.getId());
                    SkuResponse response = skuConverter.toResponse(sku,optionIds);
                    SkuGlobalConfigResponse inv = (inventoryMap != null) ? inventoryMap.get(sku.getId()) : null;

                    if (inv != null) {
                        response.setPreLimitQuantity(inv.getPreLimitQuantity());
                    } else {
                        response.setPreLimitQuantity(0);
                    }
                    return response;
                })
                .toList();
    }



    private void validateDuplicateOptions(Integer productId, Long currentSkuId, Collection<Long> submittedOptions) {
        List<SkuOptionMapping> allMappings = skuMapper.findAllOptionMappingsByProduct(productId);

        Map<Long, Set<Long>> existingSkusMap = allMappings.stream()
                .collect(Collectors.groupingBy(
                        SkuOptionMapping::getSkuId,
                        Collectors.mapping(SkuOptionMapping::getOptionId, Collectors.toSet())
                ));

        Set<Long> newOptionSet = new HashSet<>(submittedOptions);

        for (Map.Entry<Long, Set<Long>> entry : existingSkusMap.entrySet()) {
            if (entry.getKey().equals(currentSkuId)) {
                continue;
            }
            if (entry.getValue().equals(newOptionSet)) {
                throw new AppException(ErrorCode.DUPLICATE_SKU_OPTIONS);
            }
        }
    }
    public List<SkuCartDetailsDTO> getCartDetails(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<SkuCartDetailsDTO> details = skuMapper.findCartDetailsBySkuIds(skuIds);
        if (details.isEmpty()) return details;
        List<String> imagePaths = details.stream()
                .map(SkuCartDetailsDTO::getImageUrl)
                .filter(path -> path != null && !path.isEmpty())
                .distinct()
                .toList();
        Map<Long, SkuSellableDTO> inventoryMap = getInventoryInfo(skuIds);
        Map<String, String> fullImageUrlMap = getFullImageUrls(imagePaths);

        details.forEach(item -> {
            SkuSellableDTO inv = inventoryMap.get(item.getSkuId());
            if (inv != null) {
                item.setPhysicalAvailable(inv.getPhysicalAvailable());
                item.setPreLimit(inv.getPreLimit());
                item.setPreOrders(inv.getPreOrders());
            }
            if (item.getImageUrl() != null) {
                item.setImageUrl(fullImageUrlMap.getOrDefault(item.getImageUrl(), item.getImageUrl()));
            }
        });

        return details;
    }

    private Map<String, String> getFullImageUrls(List<String> paths) {
        if (paths.isEmpty()) return Collections.emptyMap();
        try {
            var response = mediaClient.getPreviewUrls(paths);
            return (response != null && response.result() != null) ? response.result() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("Failed to fetch preview URLs from MediaClient: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private Map<Long, SkuSellableDTO> getInventoryInfo(List<Long> skuIds) {
        try {
            var response = inventorySkuClient.checkSellable(skuIds);
            if (response != null && response.result() != null) {
                return response.result().stream()
                        .collect(Collectors.toMap(SkuSellableDTO::getSkuId, dto -> dto));
            }
        } catch (Exception e) {
            log.error("Failed to fetch inventory info for Cart: {}", e.getMessage());
        }
        return Collections.emptyMap();
    }




}