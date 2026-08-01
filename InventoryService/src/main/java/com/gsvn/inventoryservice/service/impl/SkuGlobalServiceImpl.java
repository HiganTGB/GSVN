package com.gsvn.inventoryservice.service.impl;

import com.gsvn.inventoryservice.converter.SkuGlobalConverter;
import com.gsvn.inventoryservice.exc.AppException;
import com.gsvn.inventoryservice.exc.ErrorCode;
import com.gsvn.inventoryservice.mapper.SkuGlobalMapper;
import com.gsvn.inventoryservice.model.dto.request.SkuGlobalSyncRequest;
import com.gsvn.inventoryservice.model.dto.response.SkuGlobalConfigResponse;
import com.gsvn.inventoryservice.model.entity.SkuGlobal;
import com.gsvn.inventoryservice.service.SkuGlobalService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SkuGlobalServiceImpl implements SkuGlobalService {

    private final SkuGlobalMapper skuGlobalMapper;
    private final SkuGlobalConverter skuGlobalConverter;

    public void syncSkuConfig(SkuGlobalSyncRequest request) {
        SkuGlobal entity = skuGlobalConverter.toEntity(request);
        skuGlobalMapper.upsertSkuGlobal(entity);
    }

    public SkuGlobalConfigResponse getBySkuId(Long skuId) {
        SkuGlobal entity = skuGlobalMapper.findBySkuId(skuId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
        return skuGlobalConverter.toResponse(entity);
    }
  

    public Map<Long, SkuGlobalConfigResponse> getBulkStatusMap(List<Long> skuIds) {
        List<SkuGlobal> entities = skuGlobalMapper.findBySkuIds(skuIds);
        return entities.stream()
                .map(skuGlobalConverter::toResponse)
                .collect(Collectors.toMap(SkuGlobalConfigResponse::getSkuId, resp -> resp));
    }
}