package com.gsvn.hrmservice.service.impl;

import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.converter.PositionConverter;
import com.gsvn.hrmservice.exc.AppException;
import com.gsvn.hrmservice.exc.DuplicateResourceException;
import com.gsvn.hrmservice.exc.ErrorCode;
import com.gsvn.hrmservice.mapper.PositionMapper;
import com.gsvn.hrmservice.model.dto.request.PositionRequest;
import com.gsvn.hrmservice.model.dto.response.PositionResponse;
import com.gsvn.hrmservice.model.entity.Position;
import com.gsvn.hrmservice.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionMapper positionMapper;
    private final PositionConverter converter;
    @Cacheable(value = "positions", key = "'all'")
    public List<PositionResponse> getAllPositions() {
        return converter.toResponseList(positionMapper.findAll()) ;
    }
    @Cacheable(value = "positions", key = "#id")
    public PositionResponse getById(Integer id) {
        Position position = getEntityById(id);
        return converter.toResponse(position);
    }
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "positions", key = "'all'"),
            @CacheEvict(value = "positions_page", allEntries = true)
    })
    public PositionResponse  create(PositionRequest request) {
        if(positionMapper.existByName(request.getPositionName())) throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY,"positionName");
        Position position= converter.toEntity(request);
        positionMapper.insert(position);
        return converter.toResponse(position);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "positions", key = "#id"),
            @CacheEvict(value = "positions", key = "'all'"),
            @CacheEvict(value = "positions_page", allEntries = true)
    })
    public PositionResponse update(Integer id, PositionRequest  request) {
        Position existingPosition = getEntityById(id);
        existingPosition= converter.updateEntity(existingPosition,request);
        positionMapper.update(existingPosition);
        return converter.toResponse(existingPosition);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "positions", allEntries = true),
            @CacheEvict(value = "positions_page", allEntries = true)
    })
    public void delete(Integer id) {
        getEntityById(id);
        positionMapper.deleteById(id);
    }
    private Position getEntityById(Integer id){
        return positionMapper.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
    }
    @Cacheable(value = "positions_page",
            key = "#keyword + ':' + #sortBy + ':' + #direction + ':' + #page + ':' + #size")
    public PageResponse<PositionResponse> getPage(String keyword, String sortBy, String direction, int page, int size) {
        page = Math.max(1, page);
        int offset = (page - 1) * size;
        List<Position> entities = positionMapper.findAdvanced(keyword, sortBy, direction, offset, size);
        long totalElements = positionMapper.countAdvanced(keyword);

        List<PositionResponse> content = converter.toResponseList(entities);

        return PageResponse.of(content,totalElements,page,size);
    }
}