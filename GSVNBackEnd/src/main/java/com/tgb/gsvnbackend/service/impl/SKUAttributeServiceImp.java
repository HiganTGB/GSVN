package com.tgb.gsvnbackend.service.impl;


import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.model.dto.SKUAttributeDTO;
import com.tgb.gsvnbackend.model.entity.SKUAttribute;
import com.tgb.gsvnbackend.model.mapper.SKUAttributeMapper;
import com.tgb.gsvnbackend.repository.jpaRepository.SKUAttributeRepository;
import com.tgb.gsvnbackend.service.CachingService;
import com.tgb.gsvnbackend.service.SKUAttributeService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SKUAttributeServiceImp implements SKUAttributeService {
      private final SKUAttributeRepository skuAttributeRepository;
      private final SKUAttributeMapper skuAttributeMapper;
      private final CachingService cachingService;
      private static final String CacheKey = "skuAttribute";

      @Autowired
      public SKUAttributeServiceImp(SKUAttributeRepository skuAttributeRepository, SKUAttributeMapper skuAttributeMapper, CachingService cachingService) {
            this.skuAttributeRepository = skuAttributeRepository;
            this.skuAttributeMapper = skuAttributeMapper;
            this.cachingService = cachingService;
            log.info("SKUAttributeServiceImp initialized.");
      }

      public SKUAttribute findEntity(int id) {
            log.info("Finding SKU attribute with ID: {}", id);
            return skuAttributeRepository.findById(id).orElseThrow(() -> {
                  log.error("SKU attribute with ID {} not found.", id);
                  return new NotFoundException("SKU attribute with id " + id + " not found.");
            });
      }

      public SKUAttributeDTO read(int id) {
            log.info("Reading SKU attribute with ID: {}", id);
            SKUAttributeDTO skuAttributeDTO = cachingService.getById(CacheKey, id, SKUAttributeDTO.class);
            if (skuAttributeDTO != null) {
                  log.info("SKU attribute with ID {} found in cache.", id);
                  return skuAttributeDTO;
            }

            log.info("SKU attribute with ID {} not found in cache. Fetching from database.", id);
            SKUAttribute skuAttribute = findEntity(id);
            skuAttributeDTO = skuAttributeMapper.toDTO(skuAttribute);
            log.info("SKU attribute with ID {} fetched from database and mapped to DTO.", id);

            cachingService.saveById(CacheKey, id, skuAttributeDTO, SKUAttributeDTO.class);
            log.info("SKU attribute with ID {} saved to cache.", id);
            return skuAttributeDTO;
      }

      public Page<SKUAttributeDTO> readByPage(int page, int size, String sortBy, String sortDirection) {
            log.info("Reading SKU attributes by page {} with size {}, sorting by {} {}", page, size, sortBy, sortDirection);
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));

            List<SKUAttributeDTO> skuAttributeDTOList = cachingService.getPageData(CacheKey, page, size, SKUAttributeDTO.class);
            if (skuAttributeDTOList != null) {
                  log.info("SKU attribute page {} with size {} found in cache.", page, size);
                  long total = skuAttributeRepository.count();
                  return new PageImpl<>(skuAttributeDTOList, pageable, total);
            }
            log.info("SKU attribute page {} with size {} not found in cache. Fetching from database.", page, size);
            Page<SKUAttribute> skuAttributePage = skuAttributeRepository.findAll(pageable);
            List<SKUAttribute> skuAttributeList = skuAttributePage.getContent();
            skuAttributeDTOList = skuAttributeList.stream().map(skuAttributeMapper::toDTO).collect(Collectors.toList());
            cachingService.setPageData(CacheKey, page, size, skuAttributeDTOList, SKUAttributeDTO.class);
            log.info("SKU attribute page {} with size {} fetched from database, mapped to DTOs, and saved to cache.", page, size);
            return new PageImpl<>(skuAttributeDTOList, pageable, skuAttributePage.getTotalElements());
      }

      public List<SKUAttributeDTO> searchByAttrs(String key, String value) {
            log.info("Searching SKU attributes where attribute '{}' contains: '{}'", key, value);
            log.info("Searching SKU attributes in database where attribute '{}' contains: '{}'", key, value);
            List<SKUAttribute> skuAttributeList = skuAttributeRepository.findByAttrsContaining(String.format("\"%s\":", key), String.format("\"%s\"", value));
            List<SKUAttributeDTO> skuAttributeDTOList = skuAttributeList.stream().map(skuAttributeMapper::toDTO).collect(Collectors.toList());
            log.info("Found {} SKU attributes in database where attribute '{}' contains: '{}'", skuAttributeDTOList.size(), key, value);
            return skuAttributeDTOList;
      }

      @Transactional
      public SKUAttributeDTO create(SKUAttributeDTO skuAttributeDTO) {
            log.info("Creating a new SKU attribute.");
            SKUAttribute skuAttribute = skuAttributeMapper.toEntity(skuAttributeDTO);
            SKUAttribute savedSKUAttribute = skuAttributeRepository.save(skuAttribute);
            SKUAttributeDTO savedSKUAttributeDTO = skuAttributeMapper.toDTO(savedSKUAttribute);
            cachingService.saveById(CacheKey, savedSKUAttribute.getId(), savedSKUAttributeDTO, SKUAttributeDTO.class);
            log.info("New SKU attribute created with ID {}, saved to cache.", savedSKUAttribute.getId());
            return savedSKUAttributeDTO;
      }

      @Transactional
      public SKUAttributeDTO update(int id, SKUAttributeDTO skuAttributeDTO) {
            log.info("Updating SKU attribute with ID: {}", id);
            SKUAttribute existingSKUAttribute = findEntity(id);
            existingSKUAttribute.setAttrs(skuAttributeDTO.getAttrs());
            SKUAttribute updatedSKUAttribute = skuAttributeRepository.save(existingSKUAttribute);
            SKUAttributeDTO updatedSKUAttributeDTO = skuAttributeMapper.toDTO(updatedSKUAttribute);
            cachingService.saveById(CacheKey, id, updatedSKUAttributeDTO, SKUAttributeDTO.class);
            log.info("SKU attribute with ID {} updated and saved to cache.", id);
            return updatedSKUAttributeDTO;
      }

      @Transactional
      public void delete(int id) {
            log.info("Deleting SKU attribute with ID: {}", id);
            SKUAttribute skuAttribute = findEntity(id);
            skuAttributeRepository.delete(skuAttribute);
            cachingService.deleteById(CacheKey, id);
            log.info("SKU attribute with ID {} deleted and removed from cache.", id);
      }
}