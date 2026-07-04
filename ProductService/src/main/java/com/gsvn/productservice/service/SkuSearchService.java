package com.gsvn.productservice.service;


import com.gsvn.productservice.mapper.SkuSearchMapper;
import com.gsvn.productservice.model.dto.SkuSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkuSearchService {

    private final SkuSearchMapper skuSearchMapper;
    public List<SkuSearchResponse> searchSkuForInventory(String keyword) {
        if (keyword == null || keyword.trim().length() < 2) {
            return Collections.emptyList();
        }
        try {
            return skuSearchMapper.quickSearchSku(keyword.trim());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    public Map<Long, SkuSearchResponse> getSkuMapByIds(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) return Collections.emptyMap();

        List<SkuSearchResponse> list = skuSearchMapper.findByIds(skuIds);

        return list.stream().collect(Collectors.toMap(
                SkuSearchResponse::getSkuId,
                sku -> sku,
                (existing, replacement) -> existing
        ));
    }
}