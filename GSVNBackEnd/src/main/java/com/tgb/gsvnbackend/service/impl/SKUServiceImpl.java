package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.model.domain.SPUDomain;
import com.tgb.gsvnbackend.model.dto.*;
import com.tgb.gsvnbackend.model.entity.SKU;

import com.tgb.gsvnbackend.model.entity.SPUSKU;

import com.tgb.gsvnbackend.model.mapper.SKUMapper;
import com.tgb.gsvnbackend.model.mapper.SPUSKUMapper;
import com.tgb.gsvnbackend.repository.jpaRepository.SKUAttributeRepository;
import com.tgb.gsvnbackend.repository.jpaRepository.SKURepository;
import com.tgb.gsvnbackend.repository.jpaRepository.SPUSKURepository;
import com.tgb.gsvnbackend.service.CachingService;
import com.tgb.gsvnbackend.service.SKUService;
import com.tgb.gsvnbackend.service.client.SPUServiceClient;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SKUServiceImpl implements SKUService {
    private final SKURepository skuRepository;
    private final SPUSKURepository spuskuRepository;

    private SKUMapper skuMapper;
    private SPUSKUMapper spuskuMapper;

    private CachingService cachingService;
    private static  final String CacheKey = "sku:";
    private static  final String CacheKeyMapping = "su:";


    private SPUServiceClient spuServiceClient;
    @Autowired
    public SKUServiceImpl(SKURepository skuRepository, SPUSKURepository spuskuRepository) {
        this.skuRepository = skuRepository;
        this.spuskuRepository = spuskuRepository;

    }
    @Transactional
    public SKUDTO create(SKUDTO skuDTO) {
        log.info("Creating a new SKU for SPU ID: {}", skuDTO.getSpuId());
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
        spuServiceClient.syncAttribute(spu_id,sku.getAttrs());

        SKUDTO savedSKUDTO = skuMapper.toDTO(savedSKU);


        cachingService.saveById(CacheKey, savedSKU.getSkuId(), savedSKUDTO, SKUDTO.class);

        log.info("SKU created with ID: {}, associated with SPU ID: {}", savedSKU.getSkuId(), spu_id);
        return savedSKUDTO;
    }
    @Transactional
    public SKUDTO update(int id, SKUDTO skuDTO) {
        log.info("Updating SKU with ID: {}", id);
        SKU existingSKU = findEntity(id);
        existingSKU.setTitle(skuDTO.getTitle());
        SKU updatedSKU = skuRepository.save(existingSKU);
        log.info("SKU with ID {} updated.", id);
        spuServiceClient.syncAttribute(updatedSKU.getSkuId(),updatedSKU.getAttrs());


        SKUDTO updatedSKUDTO = skuMapper.toDTO(updatedSKU);


        cachingService.saveById(CacheKey, id, updatedSKUDTO, SKUDTO.class);

        log.info("SKU with ID {} and its attributes saved to cache.", id);
        return updatedSKUDTO;
    }

    @Transactional
    public void delete(int id) {
        log.info("Deleting SKU with ID: {}", id);
        SKU sku = findEntity(id);
        skuRepository.delete(sku);
        cachingService.deleteById(CacheKey, id);

        log.info("SKU with ID {} deleted and removed from cache.", id);
    }
    public List<SPUSKUDTO> getListBySpuID(int spu_id)
    {
        log.info("Getting list of SPUSKUs for SPU ID: {}", spu_id);
        List<SPUSKUDTO> spuskudtos=cachingService.getListBySearchValue(CacheKeyMapping,String.valueOf(spu_id),SPUSKUDTO.class);
        if (spuskudtos != null) {
            log.info("SPUSKUs for SPU ID {} found in cache.", spu_id);
            return spuskudtos;
        }
        List<SPUSKU> spuskuList=spuskuRepository.findAllBySpuId(spu_id);
        spuskudtos = spuskuList.stream()
                .map(spuskuMapper::toDTO)
                .collect(Collectors.toList());
        cachingService.saveListBySearchValue(CacheKeyMapping, String.valueOf(spu_id), spuskudtos, SPUSKUDTO.class);
        log.info("SPUSKUs for SPU ID {} fetched from database and saved to cache.", spu_id);
        return spuskudtos;
    }
    private boolean checkExistSPU(int spu_id)
    {
        log.debug("Checking existence of SPU with ID: {}", spu_id);
        return spuServiceClient.check(spu_id);
    }
    private SKU findEntity(int id) {
        log.info("Finding SKU with ID: {}", id);
        return skuRepository.findById(id).orElseThrow(() -> {
            log.error("SKU not found with id: {}", id);
            return new NotFoundException("SKU not found with id: " + id);
        });
    }


    public SKUDTO read(int id)
    {
        log.info("Reading SKU with ID: {}", id);
        SKUDTO skudto = cachingService.getById(CacheKey, id, SKUDTO.class);
        if (skudto != null) {
            log.info("SKU with ID {} found in cache.", id);
            return skudto;
        }
        SKU sku = findEntity(id);
        skudto = skuMapper.toDTO(sku);

        cachingService.saveById(CacheKey, id, skudto, SKUDTO.class);
        log.info("SKU with ID {} fetched from database and saved to cache.", id);
        return skudto;
    }


}
