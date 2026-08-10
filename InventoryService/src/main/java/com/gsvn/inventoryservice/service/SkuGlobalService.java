package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.model.dto.request.SkuGlobalSyncRequest;
import com.gsvn.inventoryservice.model.dto.response.SkuGlobalConfigResponse;

import java.util.List;
import java.util.Map;

public interface SkuGlobalService {
    void syncSkuConfig(SkuGlobalSyncRequest request);
    SkuGlobalConfigResponse getBySkuId(Long skuId);
    Map<Long, SkuGlobalConfigResponse> getBulkStatusMap(List<Long> skuIds);
}
