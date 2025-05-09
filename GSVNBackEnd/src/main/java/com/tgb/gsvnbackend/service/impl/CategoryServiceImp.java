package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.model.dto.CategoryDTO;
import com.tgb.gsvnbackend.model.entity.Category;
import com.tgb.gsvnbackend.model.mapper.CategoryMapper;
import com.tgb.gsvnbackend.repository.CategoryRepository;
import com.tgb.gsvnbackend.service.CachingService;
import com.tgb.gsvnbackend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CachingService cachingService;
    private final String CacheKey = "category";

    @Autowired
    public CategoryServiceImp(CategoryRepository categoryRepository, CategoryMapper categoryMapper, CachingService cachingService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.cachingService = cachingService;
    }

    public Category findEntity(int id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category with id " + id + " not found."));
    }

    public CategoryDTO read(int id) {
        CategoryDTO categoryDTO = cachingService.getById(CacheKey, id, CategoryDTO.class);
        if (categoryDTO != null) {
            return categoryDTO;
        }

        Category category = findEntity(id);
        categoryDTO = categoryMapper.toDTO(category);

        cachingService.saveById(CacheKey, id, categoryDTO, CategoryDTO.class);
        return categoryDTO;
    }

    public List<CategoryDTO> readAll() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<CategoryDTO> readByPage(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        List<CategoryDTO> categoryDTOList = cachingService.getPageData(CacheKey, page, size, CategoryDTO.class);
        if (categoryDTOList != null) {
            long total = categoryRepository.count();
            return new PageImpl<>(categoryDTOList, pageable, total);
        }

        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categoryList = categoryPage.getContent();
        categoryDTOList = categoryList.stream().map(categoryMapper::toDTO).collect(Collectors.toList());
        cachingService.setPageData(CacheKey, page, size, categoryDTOList, CategoryDTO.class);
        return new PageImpl<>(categoryDTOList, pageable, categoryPage.getTotalPages());
    }

    @Transactional
    public CategoryDTO create(CategoryDTO categoryDTO) {
        if (categoryDTO.getParentID() != 0 && categoryRepository.findById(categoryDTO.getParentID()).isEmpty()) {
            throw new NotFoundException("Parent category with id " + categoryDTO.getParentID() + " not found.");
        }
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        CategoryDTO savedCategoryDTO = categoryMapper.toDTO(savedCategory);
        cachingService.saveById(CacheKey, savedCategory.getCategory_id(), savedCategoryDTO, CategoryDTO.class);
        return savedCategoryDTO;
    }

    @Transactional
    public CategoryDTO update(int id, CategoryDTO categoryDTO) {
        Category existingCategory = findEntity(id);

        if (categoryDTO.getParentID() != 0 && categoryRepository.findById(categoryDTO.getParentID()).isEmpty()) {
            throw new NotFoundException("Parent category with id " + categoryDTO.getParentID() + " not found.");
        }
        existingCategory.setTitle(categoryDTO.getTitle());
        existingCategory.setParentID(categoryDTO.getParentID());

        Category updatedCategory = categoryRepository.save(existingCategory);
        CategoryDTO updatedCategoryDTO = categoryMapper.toDTO(updatedCategory);
        cachingService.saveById(CacheKey, id, updatedCategoryDTO, CategoryDTO.class);
        return updatedCategoryDTO;
    }

    @Transactional
    public void delete(int id) {
        Category category = findEntity(id);
        categoryRepository.delete(category);
        cachingService.deleteById(CacheKey, id);
    }

    public List<CategoryDTO> getCategoriesNested() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
        return buildNestedCategories(categoryDTOs, 0);
    }

    private List<CategoryDTO> buildNestedCategories(List<CategoryDTO> categoryDTOs, int parentId) {
        List<CategoryDTO> nestedCategories = new ArrayList<>();
        for (CategoryDTO category : categoryDTOs) {
            if (category.getParentID() == parentId) {
                List<CategoryDTO> children = buildNestedCategories(categoryDTOs, category.getCategory_id());
                if (!children.isEmpty()) {
                    category.setChildren(children);
                }
                nestedCategories.add(category);
            }
        }
        return nestedCategories;
    }
}

