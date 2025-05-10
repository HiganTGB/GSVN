package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.model.dto.SPUDTO;
import com.tgb.gsvnbackend.model.entity.SPU;
import com.tgb.gsvnbackend.model.mapper.SPUMapper;
import com.tgb.gsvnbackend.repository.SPURepository;
import com.tgb.gsvnbackend.service.CachingService;
import com.tgb.gsvnbackend.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SPUServiceImp implements ProductService {
    private final SPURepository spuRepository;
    private final SPUMapper spuMapper;
    private final CachingService cachingService;
    private static final String CacheKey = "spu";
    @Autowired
    public SPUServiceImp(SPURepository spuRepository, SPUMapper spuMapper, CachingService cachingService) {
        this.spuRepository = spuRepository;
        this.spuMapper = spuMapper;
        this.cachingService = cachingService;
    }
    @Transactional
    public SPUDTO create(SPUDTO spudto) {
        SPU spu = spuMapper.toEntity(spudto);
        SPU savedSPU = spuRepository.save(spu);
        SPUDTO savedSPUDTO = spuMapper.toDTO(savedSPU);
        cachingService.saveById(CacheKey, savedSPU.getSpu_id(), savedSPUDTO, SPUDTO.class);
        return savedSPUDTO;
    }

    @Transactional
    public SPUDTO update(int id, SPUDTO spudto) {
        SPU existingSPU = findEntity(id);
        existingSPU=spuMapper.toEntity(spudto);
        SPU updatedSPU = spuRepository.save(existingSPU);
        SPUDTO updatedSPUDTO = spuMapper.toDTO(updatedSPU);
        cachingService.saveById(CacheKey, id, updatedSPUDTO, SPUDTO.class);
        return updatedSPUDTO;
    }

    @Transactional
    public void delete(int id) {
        SPU spu = findEntity(id);
        spuRepository.delete(spu);
        cachingService.deleteById(CacheKey, id);
    }

    private SPU findEntity(int id) {
        return spuRepository.findById(id).orElseThrow(() -> new RuntimeException("SPU not found with id: " + id));
    }
}