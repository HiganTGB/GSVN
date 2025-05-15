package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.model.dto.BrandDTO;
import com.tgb.gsvnbackend.model.entity.Brand;
import com.tgb.gsvnbackend.model.mapper.BrandMapper;
import com.tgb.gsvnbackend.repository.jpaRepository.BrandRepository;
import com.tgb.gsvnbackend.service.BrandService;
import com.tgb.gsvnbackend.service.CachingService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BrandServiceImp implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final CachingService cachingService;
    private static  final String CacheKey="brand";
    @Autowired
    public BrandServiceImp(BrandRepository brandRepository, BrandMapper brandMapper, CachingService cachingService) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
        this.cachingService = cachingService;
        log.info("BrandServiceImp initialized.");
    }
    public Brand findEntity(int id)
    {
        log.info("Finding brand with ID: {}", id);
        return brandRepository.findById(id).orElseThrow(()-> {
            log.error("Brand with ID {} not found.", id);
            return new NotFoundException("Brand with id " + id + " not found.");
        });
    }
    public BrandDTO read(int id)
    {
        log.info("Reading brand with ID: {}", id);
        BrandDTO brandDTO = cachingService.getById(CacheKey, id, BrandDTO.class);
        if (brandDTO != null) {
            log.info("Brand with ID {} found in cache.", id);
            return brandDTO;
        }

        log.info("Brand with ID {} not found in cache. Fetching from database.", id);
        Brand brand = findEntity(id);
        brandDTO = brandMapper.toDTO(brand);
        log.info("Brand with ID {} fetched from database and mapped to DTO.", id);

        cachingService.saveById(CacheKey, id, brandDTO, BrandDTO.class);
        log.info("Brand with ID {} saved to cache.", id);
        return brandDTO;
    }
    public Page<BrandDTO> readByPage(int page, int size,String sortBy, String sortDirection) {
        log.info("Reading brands by page {} with size {}, sorting by {} {}", page, size, sortBy, sortDirection);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));

        List<BrandDTO> brandDTOList = cachingService.getPageData(CacheKey, page, size, BrandDTO.class);
        if(brandDTOList != null){
            log.info("Brand page {} with size {} found in cache.", page, size);
            long total = brandRepository.count();
            return new PageImpl<>(brandDTOList, pageable, total);
        }
        log.info("Brand page {} with size {} not found in cache. Fetching from database.", page, size);
        Page<Brand> brandPage = brandRepository.findAll(pageable);
        List<Brand> brandList = brandPage.getContent();
        brandDTOList = brandList.stream().map(brandMapper::toDTO).collect(Collectors.toList());
        cachingService.setPageData(CacheKey, page, size, brandDTOList, BrandDTO.class);
        log.info("Brand page {} with size {} fetched from database, mapped to DTOs, and saved to cache.", page, size);
        return new PageImpl<>(brandDTOList, pageable, brandPage.getTotalElements());
    }
    public List<BrandDTO> searchByTitle(String title) {
        log.info("Searching brands by title containing: {}", title);
        List<BrandDTO> brandDTOList = cachingService.getListBySearchValue(CacheKey, title, BrandDTO.class);
        if (brandDTOList != null) {
            log.info("Brands with title containing '{}' found in cache.", title);
            return brandDTOList;
        }

        log.info("Brands with title containing '{}' not found in cache. Fetching from database.", title);
        List<Brand> brandList = brandRepository.findByTitleContaining(title);

        brandDTOList = brandList.stream()
                .map(brandMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Brands with title containing '{}' fetched from database and mapped to DTOs.", title);

        cachingService.saveListBySearchValue(CacheKey, title, brandDTOList, BrandDTO.class);
        log.info("Brands with title containing '{}' saved to cache.", title);
        return brandDTOList;
    }
    @Transactional
    public BrandDTO create(BrandDTO brandDTO) {
        log.info("Creating a new brand with title: {}", brandDTO.getTitle());
        Brand brand = brandMapper.toEntity(brandDTO);
        Brand savedBrand = brandRepository.save(brand);
        BrandDTO savedBrandDTO = brandMapper.toDTO(savedBrand);
        cachingService.saveById(CacheKey, savedBrand.getBrandId(), savedBrandDTO, BrandDTO.class);
        log.info("New brand created with ID {} and title '{}', saved to cache.", savedBrand.getBrandId(), savedBrandDTO.getTitle());
        return savedBrandDTO;
    }

    @Transactional
    public BrandDTO update(int id, BrandDTO brandDTO) {
        log.info("Updating brand with ID {}. New title: {}", id, brandDTO.getTitle());
        Brand existingBrand = findEntity(id);

        existingBrand.setTitle(brandDTO.getTitle());

        Brand updatedBrand = brandRepository.save(existingBrand);
        BrandDTO updatedBrandDTO = brandMapper.toDTO(updatedBrand);
        cachingService.saveById(CacheKey, id, updatedBrandDTO, BrandDTO.class);
        log.info("Brand with ID {} updated. New title: '{}', saved to cache.", id, updatedBrandDTO.getTitle());
        return updatedBrandDTO;
    }

    @Transactional
    public void delete(int id) {
        log.info("Deleting brand with ID: {}", id);
        Brand brand = findEntity(id);
        brandRepository.delete(brand);
        cachingService.deleteById(CacheKey, id);
        log.info("Brand with ID {} deleted and removed from cache.", id);
    }
}