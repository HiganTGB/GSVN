package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.model.dto.BrandDTO;
import com.tgb.gsvnbackend.model.entity.Brand;
import com.tgb.gsvnbackend.model.mapper.BrandMapper;
import com.tgb.gsvnbackend.repository.BrandRepository;
import com.tgb.gsvnbackend.service.BrandService;
import com.tgb.gsvnbackend.service.CachingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BrandServiceImp implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final CachingService cachingService;
    private final String CacheKey="brand";
    @Autowired
    public BrandServiceImp(BrandRepository brandRepository, BrandMapper brandMapper, CachingService cachingService) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
        this.cachingService = cachingService;
    }
    public Brand findEntity(int id)
    {
        return brandRepository.findById(id).orElseThrow(()->new NotFoundException("Brand with id " + id + " not found."));
    }
    public BrandDTO read(int id)
    {
        BrandDTO brandDTO = cachingService.getById(CacheKey, id, BrandDTO.class);
        if (brandDTO != null) {
            return brandDTO;
        }

        Brand brand = findEntity(id);
        brandDTO = brandMapper.toDTO(brand);

        cachingService.saveById(CacheKey, id, brandDTO, BrandDTO.class);
        return brandDTO;
    }
    public Page<BrandDTO> readByPage(int page, int size,String sortBy, String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));

        List<BrandDTO> brandDTOList = cachingService.getPageData(CacheKey, page, size, BrandDTO.class);
        if(brandDTOList != null){
            long total = brandRepository.count();
            return new PageImpl<>(brandDTOList, pageable, total);
        }
        Page<Brand> brandPage = brandRepository.findAll(pageable);
        List<Brand> brandList = brandPage.getContent();
        brandDTOList = brandList.stream().map(brandMapper::toDTO).collect(Collectors.toList());
        cachingService.setPageData(CacheKey, page, size, brandDTOList, BrandDTO.class);
        return new PageImpl<>(brandDTOList, pageable, brandPage.getTotalElements());
    }
    public List<BrandDTO> searchByTitle(String title) {
        List<BrandDTO> brandDTOList = cachingService.getListBySearchValue(CacheKey, title, BrandDTO.class);
        if (brandDTOList != null) {
            return brandDTOList;
        }

        List<Brand> brandList = brandRepository.findByTitleContaining(title);

        brandDTOList = brandList.stream()
                .map(brandMapper::toDTO)
                .collect(Collectors.toList());

        cachingService.saveListBySearchValue(CacheKey, title, brandDTOList, BrandDTO.class);
        return brandDTOList;
    }
    @Transactional
    public BrandDTO create(BrandDTO brandDTO) {
        Brand brand = brandMapper.toEntity(brandDTO);
        Brand savedBrand = brandRepository.save(brand);
        BrandDTO savedBrandDTO = brandMapper.toDTO(savedBrand);
        cachingService.saveById(CacheKey, savedBrand.getBrand_id(), savedBrandDTO, BrandDTO.class);
        return savedBrandDTO;
    }

    @Transactional
    public BrandDTO update(int id, BrandDTO brandDTO) {
        Brand existingBrand = findEntity(id);

        existingBrand.setTitle(brandDTO.getTitle());

        Brand updatedBrand = brandRepository.save(existingBrand);
        BrandDTO updatedBrandDTO = brandMapper.toDTO(updatedBrand);
        cachingService.saveById(CacheKey, id, updatedBrandDTO, BrandDTO.class);
        return updatedBrandDTO;
    }

    @Transactional
    public void delete(int id) {
        Brand brand = findEntity(id);
        brandRepository.delete(brand);
        cachingService.deleteById(CacheKey, id);
    }
}
