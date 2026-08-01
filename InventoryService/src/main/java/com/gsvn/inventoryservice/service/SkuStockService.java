package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.model.dto.response.SkuStockResponse;

import java.util.List;

public interface SkuStockService {
    SkuStockResponse getStock(Long skuId, Integer warehouseId);
    List<SkuStockResponse> getStockAll(Long skuId);
}
