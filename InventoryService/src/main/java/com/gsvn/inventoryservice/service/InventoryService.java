package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.client.MediaClient;
import com.gsvn.inventoryservice.client.SkuSearchInternalClient;
import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.exc.AppException;
import com.gsvn.inventoryservice.exc.ErrorCode;
import com.gsvn.inventoryservice.mapper.InventoryMapper;
import com.gsvn.inventoryservice.model.dto.InventoryDTO;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.SkuSellableDTO;
import com.gsvn.inventoryservice.model.internal.SkuSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryMapper inventoryMapper;
    private final SkuSearchInternalClient skuSearchInternalClient;
    private final MediaClient mediaClient;

    public PageResponse<InventoryDTO> getInventoryList(
            Integer warehouseId,
            Integer skuId,
            int page,
            int size,
            String sortField,
            String sortDirection) {

        int offset = (page - 1) * size;

        String validatedSortField = validateSortField(sortField, warehouseId != null);
        String validatedDirection = "ASC".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";

        List<InventoryDTO> data = inventoryMapper.findInventoryPage(
                warehouseId,
                skuId,
                size,
                offset,
                validatedSortField,
                validatedDirection
        );
        List<Long> skuIds=data.stream().map(InventoryDTO::getSkuId).toList();
        Map<Long, SkuSearchResponse> mapping= skuSearchInternalClient.getByIds(skuIds).result();
        data.forEach(item -> {
            SkuSearchResponse skuInfo = mapping.get(item.getSkuId());
            if (skuInfo != null) {
                item.setProductName(skuInfo.getProductName());
                item.setSkuCode(skuInfo.getSkuCode());
                item.setImageUrl(skuInfo.getImageUrl());
            }
        });
        List<String> imagePaths = data.stream()
                .map(InventoryDTO::getImageUrl)
                .filter(path -> path != null && !path.isBlank())
                .distinct()
                .toList();
        if (!imagePaths.isEmpty()) {

            ApiResponse<Map<String, String>> mediaResponse = mediaClient.getPreviewUrls(imagePaths);

            if (mediaResponse != null && mediaResponse.result() != null) {
                Map<String, String> urlMapping = mediaResponse.result();

                data.forEach(item -> {
                    String fullUrl = urlMapping.get(item.getImageUrl());
                    if (fullUrl != null) {
                        item.setImageUrl(fullUrl);
                    }
                });
            }
        }
        long totalElements = (warehouseId != null)
                ? inventoryMapper.countLocalStock(warehouseId, skuId)
                : inventoryMapper.countGlobalStock(skuId);

        return PageResponse.of(data,totalElements,page,size);

    }

    private String validateSortField(String field, boolean isLocal) {
        List<String> validFields = Arrays.asList(
                "sku_id", "physical_stock", "reserved_stock",
                "available_stock", "updated_at", "pre_current_orders"
        );

        if (field == null || !validFields.contains(field)) {
            return "updated_at";
        }
        return field;
    }
    public Map<Long, Boolean> getSkusSellableStatus(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) return Collections.emptyMap();

        List<SkuSellableDTO> data = inventoryMapper.checkSkusSellable(skuIds);

        return data.stream().collect(Collectors.toMap(
                SkuSellableDTO::getSkuId,
                item -> {

                    boolean hasPhysical = item.getPhysicalAvailable() > 0;

                    boolean hasPreorder = item.getPreLimit() > 0 && item.getPreOrders() < item.getPreLimit();

                    return hasPhysical || hasPreorder;
                }
        ));
    }
    public List<SkuSellableDTO> getSkusSellable(List<Long> skuIds) {
        return inventoryMapper.checkSkusSellable(skuIds);
    }



    @Transactional
    public void processReadyToPick(String skuCode, Integer warehouseId, Integer quantity) {
        int globalUpdated = inventoryMapper.decreaseReservedGlobal(skuCode, quantity);
        if (globalUpdated == 0) {
            throw new AppException(ErrorCode.NOT_ALLOW);
        }
        inventoryMapper.increaseReservedLocal(skuCode, warehouseId, quantity);
    }
    @Transactional
    public void processPacked(String skuCode, Integer warehouseId, Integer quantity) {
        int updated = inventoryMapper.decreasePhysicalAndReservedLocal(skuCode, warehouseId, quantity);
        if (updated == 0) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
    }
    @Transactional
    public void processShipmentPacked(Integer warehouseId, Map<String, Integer> items) {
        items.forEach((skuCode, qty) -> processPacked(skuCode, warehouseId, qty));
    }

}