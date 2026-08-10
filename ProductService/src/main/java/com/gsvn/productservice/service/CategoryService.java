package com.gsvn.productservice.service;

import com.gsvn.productservice.converter.CategoryConverter;
import com.gsvn.productservice.exc.AppException;
import com.gsvn.productservice.exc.ErrorCode;
import com.gsvn.productservice.mapper.CategoryMapper;
import com.gsvn.productservice.model.dto.request.CategoryRequest;
import com.gsvn.productservice.model.dto.response.CategoryResponse;
import com.gsvn.productservice.common.PageResponse;
import com.gsvn.productservice.model.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface CategoryService {



    public PageResponse<CategoryResponse> getPage(String keyword, int page,
                                                  int size
    );

    public List<CategoryResponse> getCategoryTree();
    public List<CategoryResponse> getList();

    public CategoryResponse getById(Integer id);

    @Transactional
    public CategoryResponse create(CategoryRequest request) ;

    @Transactional
    public CategoryResponse update(Integer id, CategoryRequest request) ;

    @Transactional
    public void delete(Integer id);
}