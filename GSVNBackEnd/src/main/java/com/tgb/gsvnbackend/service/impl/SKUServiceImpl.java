package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.model.domain.SPUDomain;
import com.tgb.gsvnbackend.model.dto.*;
import com.tgb.gsvnbackend.model.entity.SKU;
import com.tgb.gsvnbackend.model.entity.SKUAttribute;
import com.tgb.gsvnbackend.model.entity.SPUSKU;
import com.tgb.gsvnbackend.model.mapper.SKUAttributeMapper;
import com.tgb.gsvnbackend.model.mapper.SKUMapper;
import com.tgb.gsvnbackend.model.mapper.SPUSKUMapper;
import com.tgb.gsvnbackend.repository.jpaRepository.SKUAttributeRepository;
import com.tgb.gsvnbackend.repository.jpaRepository.SKURepository;
import com.tgb.gsvnbackend.repository.jpaRepository.SPUSKURepository;
import com.tgb.gsvnbackend.service.CachingService;
import com.tgb.gsvnbackend.service.SKUService;
import com.tgb.gsvnbackend.service.client.SPUServiceClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SKUServiceImpl implements SKUService {
    private final SKURepository skuRepository;
    private final SPUSKURepository spuskuRepository;
    private final SKUAttributeRepository skuAttributeRepository;
    private SKUMapper skuMapper;
    private SPUSKUMapper spuskuMapper;
    private SKUAttributeMapper attributeMapper;
    private CachingService cachingService;
    private static  final String CacheKey = "sku:";
    private static  final String CacheKeyMapping = "su:";
    private static  final String CacheKeyAttribute = "sku_att:";

    private SPUServiceClient spuServiceClient;
    @Autowired
    public SKUServiceImpl(SKURepository skuRepository, SPUSKURepository spuskuRepository, SKUAttributeRepository skuAttributeRepository) {
        this.skuRepository = skuRepository;
        this.spuskuRepository = spuskuRepository;
        this.skuAttributeRepository = skuAttributeRepository;
    }
    @Transactional
    public SKUDTO create(SKUDTO skuDTO) {
        int spu_id=skuDTO.getSpuId();
        SPUDomain spu=spuServiceClient.readDomain(spu_id);
        SKU sku = skuMapper.toEntity(skuDTO);
        sku.setCategoryId(sku.getCategoryId());
        sku.setBrandId(sku.getBrandId());
        sku.setFandomId(sku.getFandomId());
        SKU savedSKU = skuRepository.save(sku);
        SPUSKU spusku= SPUSKU.builder()
                .skuId(sku.getSkuId())
                .spuId(spu_id)
                .build();
        spuskuRepository.save(spusku);
        SKUAttribute attributes=SKUAttribute.builder()
                .attrs(skuDTO.getAttrs())
                .build();
        SKUAttribute savedAttributes=skuAttributeRepository.save(attributes);

        SKUDTO savedSKUDTO = skuMapper.toDTO(savedSKU);
        SKUAttributeDTO savedAttributesDTO=attributeMapper.toDTO(savedAttributes);

        cachingService.saveById(CacheKey, savedSKU.getSkuId(), savedSKUDTO, SKUDTO.class);
        cachingService.saveById(CacheKeyAttribute,savedSKU.getSkuId(),savedAttributesDTO, SKUAttributeDTO.class);
        return savedSKUDTO;
    }
    @Transactional
    public SKUDTO update(int id, SKUDTO skuDTO) {
        SKU existingSKU = findEntity(id);
        existingSKU.setTitle(skuDTO.getTitle());
        SKU updatedSKU = skuRepository.save(existingSKU);

        SKUAttribute skuAttrs=findAttribute(id);
        skuAttrs.setAttrs(skuDTO.getAttrs());
        SKUAttribute updatedAttributes=skuAttributeRepository.save(skuAttrs);

        SKUDTO updatedSKUDTO = skuMapper.toDTO(updatedSKU);
        SKUAttributeDTO updatedAttributesDTO=attributeMapper.toDTO(updatedAttributes);

        cachingService.saveById(CacheKey, id, updatedSKUDTO, SKUDTO.class);
        cachingService.saveById(CacheKeyAttribute,id,updatedAttributesDTO, SKUAttributeDTO.class);
        return updatedSKUDTO;
    }

    @Transactional
    public void delete(int id) {
        SKU sku = findEntity(id);
        skuRepository.delete(sku);
        cachingService.deleteById(CacheKey, id);
    }
    public List<SPUSKUDTO> getListBySpuID(int spu_id)
    {
        List<SPUSKUDTO> spuskudtos=cachingService.getListBySearchValue(CacheKeyMapping,String.valueOf(spu_id),SPUSKUDTO.class);
        if (spuskudtos != null) {
            return spuskudtos;
        }
        List<SPUSKU> spuskuList=spuskuRepository.findAllBySpuId(spu_id);
        spuskudtos = spuskuList.stream()
                .map(spuskuMapper::toDTO)
                .collect(Collectors.toList());
        cachingService.saveListBySearchValue(CacheKeyMapping, String.valueOf(spu_id), spuskudtos, SPUSKUDTO.class);
        return spuskudtos;
    }
    private boolean checkExistSPU(int spu_id)
    {
        return spuServiceClient.check(spu_id);
    }
    private SKU findEntity(int id) {
        return skuRepository.findById(id).orElseThrow(() -> new NotFoundException("SKU not found with id: " + id));
    }
    private SKUAttribute findAttribute(int id) {
        return skuAttributeRepository.findById(id).orElseThrow(() -> new NotFoundException("SKU not found with id: " + id));
    }
    public SKUAttributeDTO readAttribute(int id)
    {
        SKUAttributeDTO attributeDTO = cachingService.getById(CacheKeyAttribute, id, SKUAttributeDTO.class);
        if (attributeDTO != null) {
            return attributeDTO;
        }
        SKUAttribute attribute = findAttribute(id);
        attributeDTO = attributeMapper.toDTO(attribute);
        cachingService.saveById(CacheKeyAttribute, id, attributeDTO, SKUAttributeDTO.class);
        return attributeDTO;

    }
    public SKUDTO read(int id)
    {
        SKUDTO skudto = cachingService.getById(CacheKey, id, SKUDTO.class);
        if (skudto != null) {
            return skudto;
        }
        SKU sku = findEntity(id);
        skudto = skuMapper.toDTO(sku);

        cachingService.saveById(CacheKey, id, skudto, SKUDTO.class);
        return skudto;
    }


}
