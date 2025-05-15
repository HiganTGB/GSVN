package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.model.dto.FandomDTO;
import com.tgb.gsvnbackend.model.entity.Fandom;
import com.tgb.gsvnbackend.model.mapper.FandomMapper;
import com.tgb.gsvnbackend.repository.jpaRepository.FandomRepository;
import com.tgb.gsvnbackend.service.CachingService;
import com.tgb.gsvnbackend.service.FandomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FandomServiceImp implements FandomService {
    private final FandomRepository fandomRepository;
    private final FandomMapper fandomMapper;
    private final CachingService cachingService;
    private static  final String CacheKey = "fandom";

    @Autowired
    public FandomServiceImp(FandomRepository fandomRepository, FandomMapper fandomMapper, CachingService cachingService) {
        this.fandomRepository = fandomRepository;
        this.fandomMapper = fandomMapper;
        this.cachingService = cachingService;
        log.info("FandomServiceImp initialized.");
    }

    public Fandom findEntity(int id) {
        log.info("Finding fandom with ID: {}", id);
        return fandomRepository.findById(id).orElseThrow(() -> {
            log.error("Fandom with ID {} not found.", id);
            return new NotFoundException("Fandom with id " + id + " not found.");
        });
    }

    public FandomDTO read(int id) {
        log.info("Reading fandom with ID: {}", id);
        FandomDTO fandomDTO = cachingService.getById(CacheKey, id, FandomDTO.class);
        if (fandomDTO != null) {
            log.info("Fandom with ID {} found in cache.", id);
            return fandomDTO;
        }

        log.info("Fandom with ID {} not found in cache. Fetching from database.", id);
        Fandom fandom = findEntity(id);
        fandomDTO = fandomMapper.toDTO(fandom);
        log.info("Fandom with ID {} fetched from database and mapped to DTO.", id);

        cachingService.saveById(CacheKey, id, fandomDTO, FandomDTO.class);
        log.info("Fandom with ID {} saved to cache.", id);
        return fandomDTO;
    }

    public List<FandomDTO> readAll() {
        log.info("Reading all fandoms.");
        List<Fandom> fandomList = fandomRepository.findAll();
        List<FandomDTO> fandomDTOs = fandomList.stream()
                .map(fandomMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Found {} fandoms.", fandomDTOs.size());
        return fandomDTOs;
    }
    public Page<FandomDTO> readByPage(int page, int size,String sortBy, String sortDirection) {
        log.info("Reading fandoms by page {} with size {}, sorting by {} {}", page, size, sortBy, sortDirection);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        List<FandomDTO> fandomDTOList = cachingService.getPageData(CacheKey,  pageable.getPageNumber() + 1, pageable.getPageSize(), FandomDTO.class);
        if (fandomDTOList != null) {
            log.info("Fandom page {} with size {} found in cache.", page, size);
            long total = fandomRepository.count(); // Get total count from the repository
            return new PageImpl<>(fandomDTOList, pageable, total);
        }


        log.info("Fandom page {} with size {} not found in cache. Fetching from database.", page, size);
        Page<Fandom> fandomPage = fandomRepository.findAll(pageable);
        List<Fandom> fandomList = fandomPage.getContent();
        fandomDTOList = fandomList.stream().map(fandomMapper::toDTO).collect(Collectors.toList());


        cachingService.setPageData(CacheKey, pageable.getPageNumber() + 1, pageable.getPageSize(), fandomDTOList, FandomDTO.class);
        log.info("Fandom page {} with size {} fetched from database, mapped to DTOs, and saved to cache.", page, size);
        return new PageImpl<>(fandomDTOList, pageable, fandomPage.getTotalPages());
    }
    public List<FandomDTO> searchByTitle(String title) {
        log.info("Searching fandoms by title containing: {}", title);
        List<FandomDTO> fandomDTOList = cachingService.getListBySearchValue(CacheKey, title, FandomDTO.class);
        if (fandomDTOList != null) {
            log.info("Fandoms with title containing '{}' found in cache.", title);
            return fandomDTOList;
        }

        log.info("Fandoms with title containing '{}' not found in cache. Fetching from database.", title);
        List<Fandom> fandomList = fandomRepository.findByTitleContaining(title);

        fandomDTOList = fandomList.stream()
                .map(fandomMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Fandoms with title containing '{}' fetched from database and mapped to DTOs.", title);

        cachingService.saveListBySearchValue(CacheKey, title, fandomDTOList, FandomDTO.class);
        log.info("Fandoms with title containing '{}' saved to cache.", title);
        return fandomDTOList;
    }
    @Transactional
    public FandomDTO create(FandomDTO fandomDTO) {
        log.info("Creating a new fandom with title: {}", fandomDTO.getTitle());
        Fandom fandom = fandomMapper.toEntity(fandomDTO);
        Fandom savedFandom = fandomRepository.save(fandom);
        FandomDTO savedFandomDTO = fandomMapper.toDTO(savedFandom);
        cachingService.saveById(CacheKey, savedFandom.getFandomId(), savedFandomDTO, FandomDTO.class);
        log.info("New fandom created with ID {} and title '{}', saved to cache.", savedFandom.getFandomId(), savedFandomDTO.getTitle());
        return savedFandomDTO;
    }

    @Transactional
    public FandomDTO update(int id, FandomDTO fandomDTO) {
        log.info("Updating fandom with ID {}. New title: {}", id, fandomDTO.getTitle());
        Fandom existingFandom = findEntity(id);
        existingFandom.setTitle(fandomDTO.getTitle());

        Fandom updatedFandom = fandomRepository.save(existingFandom);
        FandomDTO updatedFandomDTO = fandomMapper.toDTO(updatedFandom);
        cachingService.saveById(CacheKey, id, updatedFandomDTO, FandomDTO.class);
        log.info("Fandom with ID {} updated. New title: '{}', saved to cache.", id, updatedFandomDTO.getTitle());
        return updatedFandomDTO;
    }

    @Transactional
    public void delete(int id) {
        log.info("Deleting fandom with ID: {}", id);
        Fandom fandom = findEntity(id);
        fandomRepository.delete(fandom);
        cachingService.deleteById(CacheKey, id);
        log.info("Fandom with ID {} deleted and removed from cache.", id);
    }
}