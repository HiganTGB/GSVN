package com.gsvn.productservice.service.impl;

import com.gsvn.productservice.converter.BrandConverter;
import com.gsvn.productservice.exc.AppException;
import com.gsvn.productservice.exc.ErrorCode;
import com.gsvn.productservice.mapper.BrandMapper;
import com.gsvn.productservice.model.dto.request.BrandRequest;
import com.gsvn.productservice.model.dto.response.BrandResponse;
import com.gsvn.productservice.common.PageResponse;
import com.gsvn.productservice.model.entity.Brand;
import com.gsvn.productservice.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandMapper brandMapper;
    private final BrandConverter brandConverter;


    public PageResponse<BrandResponse> getPage(String keyword,
                                               String sortBy,
                                               String direction,
                                               int page,
                                               int size
    ) {

        int offset = (page - 1) * size;

        String sortField = switch (sortBy != null ? sortBy : "") {
            case "name" -> "name";
            default -> "created_at";
        };

        String sortOrder = "asc".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        List<Brand> entities = brandMapper.findPage(
                keyword,
                offset,
                size,
                sortField,
                sortOrder
        );

        long totalElements = brandMapper.countSearch(keyword);
        List<BrandResponse> content = brandConverter.toResponseList(entities);

        return PageResponse.of(content,totalElements,page,size);
    }
    public List<BrandResponse> getList() {
        List<Brand> brands = brandMapper.findAll();
        return brandConverter.toResponseList(brands);
    }


    public BrandResponse getById(Integer id) {
        Brand brand = brandMapper.findById(id);
        if (brand == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
        return brandConverter.toResponse(brand);
    }


    @Transactional
    public BrandResponse create(BrandRequest request) {
        Brand entity = brandConverter.toEntity(request);
        brandMapper.insert(entity);
        return getById(entity.getId());
    }

    @Transactional
    public BrandResponse update(Integer id, BrandRequest request) {
        Brand entity = brandMapper.findById(id);
        if (entity == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }

        brandConverter.updateEntity(request, entity);
        brandMapper.update(entity);

        return getById(id);
    }


    @Transactional
    public void delete(Integer id) {

        if (brandMapper.findById(id) == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
        brandMapper.delete(id);
    }
}