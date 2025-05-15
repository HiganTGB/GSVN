package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.model.dto.FandomDTO;
import com.tgb.gsvnbackend.model.entity.Fandom;
import com.tgb.gsvnbackend.model.mapper.FandomMapper;
import com.tgb.gsvnbackend.repository.jpaRepository.FandomRepository;
import com.tgb.gsvnbackend.service.CachingService;
import com.tgb.gsvnbackend.service.FandomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
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
    }

    public Fandom findEntity(int id) {
        return fandomRepository.findById(id).orElseThrow(() -> new NotFoundException("Fandom with id " + id + " not found."));
    }

    public FandomDTO read(int id) {
        FandomDTO fandomDTO = cachingService.getById(CacheKey, id, FandomDTO.class);
        if (fandomDTO != null) {
            return fandomDTO;
        }

        Fandom fandom = findEntity(id);
        fandomDTO = fandomMapper.toDTO(fandom);

        cachingService.saveById(CacheKey, id, fandomDTO, FandomDTO.class);
        return fandomDTO;
    }

    public List<FandomDTO> readAll() {
        List<Fandom> fandomList = fandomRepository.findAll();
        return fandomList.stream()
                .map(fandomMapper::toDTO)
                .collect(Collectors.toList());
    }
    public Page<FandomDTO> readByPage(int page, int size,String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        List<FandomDTO> fandomDTOList = cachingService.getPageData(CacheKey,  pageable.getPageNumber() + 1, pageable.getPageSize(), FandomDTO.class);
        if (fandomDTOList != null) {
            long total = fandomRepository.count(); // Get total count from the repository
            return new PageImpl<>(fandomDTOList, pageable, total);
        }


        Page<Fandom> fandomPage = fandomRepository.findAll(pageable);
        List<Fandom> fandomList = fandomPage.getContent();
        fandomDTOList = fandomList.stream().map(fandomMapper::toDTO).collect(Collectors.toList());


        cachingService.setPageData(CacheKey, pageable.getPageNumber() + 1, pageable.getPageSize(), fandomDTOList, FandomDTO.class);
        return new PageImpl<>(fandomDTOList, pageable, fandomPage.getTotalPages());
    }
    public List<FandomDTO> searchByTitle(String title) {

        List<FandomDTO> fandomDTOList = cachingService.getListBySearchValue(CacheKey, title, FandomDTO.class);
        if (fandomDTOList != null) {
            return fandomDTOList;
        }

        List<Fandom> fandomList = fandomRepository.findByTitleContaining(title);

        fandomDTOList = fandomList.stream()
                .map(fandomMapper::toDTO)
                .collect(Collectors.toList());

        cachingService.saveListBySearchValue(CacheKey, title, fandomDTOList, FandomDTO.class);
        return fandomDTOList;
    }
    @Transactional
    public FandomDTO create(FandomDTO fandomDTO) {
        Fandom fandom = fandomMapper.toEntity(fandomDTO);
        Fandom savedFandom = fandomRepository.save(fandom);
        FandomDTO savedFandomDTO = fandomMapper.toDTO(savedFandom);
        cachingService.saveById(CacheKey, savedFandom.getFandomId(), savedFandomDTO, FandomDTO.class);
        return savedFandomDTO;
    }

    @Transactional
    public FandomDTO update(int id, FandomDTO fandomDTO) {
        Fandom existingFandom = findEntity(id);
        existingFandom.setTitle(fandomDTO.getTitle());

        Fandom updatedFandom = fandomRepository.save(existingFandom);
        FandomDTO updatedFandomDTO = fandomMapper.toDTO(updatedFandom);
        cachingService.saveById(CacheKey, id, updatedFandomDTO, FandomDTO.class);
        return updatedFandomDTO;
    }

    @Transactional
    public void delete(int id) {
        Fandom fandom = findEntity(id);
        fandomRepository.delete(fandom);
        cachingService.deleteById(CacheKey, id);
    }
}

