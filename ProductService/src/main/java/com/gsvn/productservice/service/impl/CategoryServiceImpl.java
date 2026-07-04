package com.gsvn.productservice.service.impl;

import com.gsvn.productservice.converter.CategoryConverter;
import com.gsvn.productservice.exc.AppException;
import com.gsvn.productservice.exc.ErrorCode;
import com.gsvn.productservice.mapper.CategoryMapper;
import com.gsvn.productservice.model.dto.request.CategoryRequest;
import com.gsvn.productservice.model.dto.response.CategoryResponse;
import com.gsvn.productservice.common.PageResponse;
import com.gsvn.productservice.model.entity.Category;
import com.gsvn.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryConverter categoryConverter;


    public PageResponse<CategoryResponse> getPage(String keyword, int page,
                                                  int size
    ) {

        int offset = (page - 1) * size;
        List<Category> entities = categoryMapper.findPage(
                keyword,
                offset,
                size
        );

        long totalElements = categoryMapper.countSearch(keyword);

        List<CategoryResponse> content = categoryConverter.toResponseList(entities);

        return PageResponse.of(content, totalElements, page, size);
    }

    public List<CategoryResponse> getCategoryTree() {
        List<Category> rootCategories = categoryMapper.findRootCategories();
        return categoryConverter.toResponseList(rootCategories);
    }
    public List<CategoryResponse> getList() {
        List<Category> categories = categoryMapper.findAll();
        return categoryConverter.toResponseList(categories);
    }

    public CategoryResponse getById(Integer id) {
        Category entity = categoryMapper.findById(id);
        if (entity == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
        return categoryConverter.toResponse(entity);
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {

        if (request.getParentCategoryId() != null) {
            if (categoryMapper.findById(request.getParentCategoryId()) == null) {
                throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
            }
        }

        Category entity = categoryConverter.toEntity(request);
        categoryMapper.insert(entity);

        return getById(entity.getId());
    }

    @Transactional
    public CategoryResponse update(Integer id, CategoryRequest request) {
        Category entity = categoryMapper.findById(id);
        if (entity == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }


        if (id.equals(request.getParentCategoryId())) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setParentCategoryId(request.getParentCategoryId());

        categoryMapper.update(entity);
        return getById(id);
    }

    @Transactional
    public void delete(Integer id) {
        Category entity = categoryMapper.findById(id);
        if (entity == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }


        Category withChildren = categoryMapper.findWithChildren(id);
        if (withChildren != null && withChildren.getSubCategories() != null
                && !withChildren.getSubCategories().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

    }
}