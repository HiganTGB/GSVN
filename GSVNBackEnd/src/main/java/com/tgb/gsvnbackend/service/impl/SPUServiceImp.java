package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.model.domain.SPUDomain;
import com.tgb.gsvnbackend.model.dto.SPUDTO;
import com.tgb.gsvnbackend.model.entity.SPU;
import com.tgb.gsvnbackend.model.mapper.SPUMapper;
import com.tgb.gsvnbackend.repository.jpaRepository.SPURepository;
import com.tgb.gsvnbackend.service.CachingService;

import com.tgb.gsvnbackend.service.SPUService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SPUServiceImp implements SPUService {
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
        cachingService.saveById(CacheKey, savedSPU.getSpuId(), savedSPUDTO, SPUDTO.class);
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

    @Override
    public boolean exists(int id) {
        return spuRepository.existsById(id);
    }

    private SPU findEntity(int id) {
        return spuRepository.findById(id).orElseThrow(() -> new RuntimeException("SPU not found with id: " + id));
    }
    public SPUDomain getDomain(int id)
    {
       return spuMapper.toDomain(findEntity(id));
    }
}