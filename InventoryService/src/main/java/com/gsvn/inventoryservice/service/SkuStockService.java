package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.converter.SkuStockConverter;
import com.gsvn.inventoryservice.mapper.SkuStockMapper;
import com.gsvn.inventoryservice.model.dto.response.SkuStockResponse;
import com.gsvn.inventoryservice.model.entity.SkuStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkuStockService {
    private final SkuStockMapper skuStockMapper;
    private final SkuStockConverter skuStockConverter;
    public SkuStockResponse getStock(Long skuId, Integer warehouseId) {
        SkuStock stock = skuStockMapper.findByIdAndWarehouse(skuId, warehouseId);
        if (stock == null) {
            return skuStockConverter.toResponse(new SkuStock(skuId,null,warehouseId,0,0,0, OffsetDateTime.now()));
        }
        return skuStockConverter.toResponse(stock);
    }
    public List<SkuStockResponse> getStockAll(Long skuId) {
        List<SkuStock> stocks = skuStockMapper.findById(skuId);
        return skuStockConverter.toResponseList(stocks);
    }
}
