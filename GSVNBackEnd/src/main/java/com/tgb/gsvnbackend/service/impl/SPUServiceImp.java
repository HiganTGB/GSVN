package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.model.domain.SPUDomain;
import com.tgb.gsvnbackend.model.dto.SPUDTO;
import com.tgb.gsvnbackend.model.entity.SPU;
import com.tgb.gsvnbackend.model.mapper.SPUMapper;
import com.tgb.gsvnbackend.repository.jpaRepository.SPURepository;
import com.tgb.gsvnbackend.service.CachingService;
import com.tgb.gsvnbackend.service.SPUService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SPUServiceImp implements SPUService {
    private final SPURepository spuRepository;
    private SPUMapper spuMapper;
    private final CachingService cachingService;
    private static final String CacheKey = "spu";
    @Autowired
    public SPUServiceImp(SPURepository spuRepository, CachingService cachingService) {
        this.spuRepository = spuRepository;
        this.cachingService = cachingService;
        log.info("SPUServiceImp initialized.");
    }
    @Transactional
    public SPUDTO create(SPUDTO spudto) {
        log.info("Creating a new SPU with title: {}", spudto.getTitle());
        SPU spu = spuMapper.toEntity(spudto);
        SPU savedSPU = spuRepository.save(spu);
        SPUDTO savedSPUDTO = spuMapper.toDTO(savedSPU);
        cachingService.saveById(CacheKey, savedSPU.getSpuId(), savedSPUDTO, SPUDTO.class);
        log.info("SPU created with ID: {}, title: '{}', saved to cache.", savedSPU.getSpuId(), savedSPUDTO.getTitle());
        return savedSPUDTO;
    }

    @Transactional
    public SPUDTO update(int id, SPUDTO spudto) {
        log.info("Updating SPU with ID: {}", id);
        SPU existingSPU = findEntity(id);
        SPU updatedSPU = spuMapper.toEntity(spudto);
        updatedSPU.setSpuId(existingSPU.getSpuId()); // Ensure ID is not overwritten
        updatedSPU = spuRepository.save(updatedSPU);
        SPUDTO updatedSPUDTO = spuMapper.toDTO(updatedSPU);
        cachingService.saveById(CacheKey, id, updatedSPUDTO, SPUDTO.class);
        log.info("SPU with ID {} updated, title: '{}', saved to cache.", id, updatedSPUDTO.getTitle());
        return updatedSPUDTO;
    }

    @Transactional
    public void delete(int id) {
        log.info("Deleting SPU with ID: {}", id);
        SPU spu = findEntity(id);
        spuRepository.delete(spu);
        cachingService.deleteById(CacheKey, id);
        log.info("SPU with ID {} deleted and removed from cache.", id);
    }

    @Override
    public boolean exists(int id) {
        log.debug("Checking if SPU exists with ID: {}", id);
        boolean exists = spuRepository.existsById(id);
        log.debug("SPU with ID {} exists: {}", id, exists);
        return exists;
    }

    private SPU findEntity(int id) {
        log.info("Finding SPU with ID: {}", id);
        return spuRepository.findById(id).orElseThrow(() -> {
            log.error("SPU not found with id: {}", id);
            return new RuntimeException("SPU not found with id: " + id);
        });
    }
    public SPUDomain getDomain(int id)
    {
        log.info("Getting SPU domain for ID: {}", id);
        SPU spu = findEntity(id);
        SPUDomain spuDomain = spuMapper.toDomain(spu);
        log.debug("SPU domain for ID {}: {}", id, spuDomain);
        return spuDomain;
    }
}