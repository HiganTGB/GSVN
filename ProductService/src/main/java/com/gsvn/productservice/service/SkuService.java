package com.gsvn.productservice.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public interface SkuService {

    public SkuResponse createSku(Integer productId, SkuRequest request);
    public SkuResponse updateSku( Long skuId, SkuRequest request);
    public List<SkuResponse> getSkusByProduct(Integer productId);
    public List<SkuCartDetailsDTO> getCartDetails(List<Long> skuIds) ;
}