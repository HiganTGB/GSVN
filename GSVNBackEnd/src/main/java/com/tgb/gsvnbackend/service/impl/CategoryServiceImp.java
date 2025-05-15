package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.model.dto.CategoryDTO;
import com.tgb.gsvnbackend.model.entity.Category;
import com.tgb.gsvnbackend.model.mapper.CategoryMapper;
import com.tgb.gsvnbackend.repository.jpaRepository.CategoryRepository;
import com.tgb.gsvnbackend.service.CachingService;
import com.tgb.gsvnbackend.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CachingService cachingService;
    private static  final String CacheKey = "category";

    @Autowired
    public CategoryServiceImp(CategoryRepository categoryRepository, CategoryMapper categoryMapper, CachingService cachingService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.cachingService = cachingService;
        log.info("CategoryServiceImp initialized.");
    }

    public Category findEntity(int id) {
        log.info("Finding category with ID: {}", id);
        return categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Category with ID {} not found.", id);
            return new NotFoundException("Category with id " + id + " not found.");
        });
    }

    public CategoryDTO read(int id) {
        log.info("Reading category with ID: {}", id);
        CategoryDTO categoryDTO = cachingService.getById(CacheKey, id, CategoryDTO.class);
        if (categoryDTO != null) {
            log.info("Category with ID {} found in cache.", id);
            return categoryDTO;
        }

        log.info("Category with ID {} not found in cache. Fetching from database.", id);
        Category category = findEntity(id);
        categoryDTO = categoryMapper.toDTO(category);
        log.info("Category with ID {} fetched from database and mapped to DTO.", id);

        cachingService.saveById(CacheKey, id, categoryDTO, CategoryDTO.class);
        log.info("Category with ID {} saved to cache.", id);
        return categoryDTO;
    }

    public List<CategoryDTO> readAll() {
        log.info("Reading all categories.");
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOs = categoryList.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Found {} categories.", categoryDTOs.size());
        return categoryDTOs;
    }

    public Page<CategoryDTO> readByPage(int page, int size, String sortBy, String sortDirection) {
        log.info("Reading categories by page {} with size {}, sorting by {} {}", page, size, sortBy, sortDirection);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        List<CategoryDTO> categoryDTOList = cachingService.getPageData(CacheKey, page, size, CategoryDTO.class);
        if (categoryDTOList != null) {
            log.info("Category page {} with size {} found in cache.", page, size);
            long total = categoryRepository.count();
            return new PageImpl<>(categoryDTOList, pageable, total);
        }

        log.info("Category page {} with size {} not found in cache. Fetching from database.", page, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categoryList = categoryPage.getContent();
        categoryDTOList = categoryList.stream().map(categoryMapper::toDTO).collect(Collectors.toList());
        cachingService.setPageData(CacheKey, page, size, categoryDTOList, CategoryDTO.class);
        log.info("Category page {} with size {} fetched from database, mapped to DTOs, and saved to cache.", page, size);
        return new PageImpl<>(categoryDTOList, pageable, categoryPage.getTotalPages());
    }

    @Transactional
    public CategoryDTO create(CategoryDTO categoryDTO) {
        log.info("Creating a new category with title: {}", categoryDTO.getTitle());
        if (categoryDTO.getParentId() != 0 && categoryRepository.findById(categoryDTO.getParentId()).isEmpty()) {
            log.error("Parent category with id {} not found.", categoryDTO.getParentId());
            throw new NotFoundException("Parent category with id " + categoryDTO.getParentId() + " not found.");
        }
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        CategoryDTO savedCategoryDTO = categoryMapper.toDTO(savedCategory);
        cachingService.saveById(CacheKey, savedCategory.getCategoryId(), savedCategoryDTO, CategoryDTO.class);
        log.info("New category created with ID {} and title '{}', saved to cache.", savedCategory.getCategoryId(), savedCategoryDTO.getTitle());
        return savedCategoryDTO;
    }

    @Transactional
    public CategoryDTO update(int id, CategoryDTO categoryDTO) {
        log.info("Updating category with ID {}. New title: {}, Parent ID: {}", id, categoryDTO.getTitle(), categoryDTO.getParentId());
        Category existingCategory = findEntity(id);

        if (categoryDTO.getParentId() != 0 && categoryRepository.findById(categoryDTO.getParentId()).isEmpty()) {
            log.error("Parent category with id {} not found.", categoryDTO.getParentId());
            throw new NotFoundException("Parent category with id " + categoryDTO.getParentId() + " not found.");
        }
        existingCategory.setTitle(categoryDTO.getTitle());
        existingCategory.setParentId(categoryDTO.getParentId());

        Category updatedCategory = categoryRepository.save(existingCategory);
        CategoryDTO updatedCategoryDTO = categoryMapper.toDTO(updatedCategory);
        cachingService.saveById(CacheKey, id, updatedCategoryDTO, CategoryDTO.class);
        log.info("Category with ID {} updated. New title: '{}', Parent ID: {}, saved to cache.", id, updatedCategoryDTO.getTitle(), updatedCategoryDTO.getParentId());
        return updatedCategoryDTO;
    }

    @Transactional
    public void delete(int id) {
        log.info("Deleting category with ID: {}", id);
        Category category = findEntity(id);
        categoryRepository.delete(category);
        cachingService.deleteById(CacheKey, id);
        log.info("Category with ID {} deleted and removed from cache.", id);
    }

    public List<CategoryDTO> getCategoriesNested() {
        log.info("Fetching categories in nested structure.");
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
        List<CategoryDTO> nestedCategories = buildNestedCategories(categoryDTOs, 0);
        log.info("Built nested category structure with {} top-level categories.", nestedCategories.size());
        return nestedCategories;
    }

    private List<CategoryDTO> buildNestedCategories(List<CategoryDTO> categoryDTOs, int parentId) {
        List<CategoryDTO> nestedCategories = new ArrayList<>();
        for (CategoryDTO category : categoryDTOs) {
            if (category.getParentId() == parentId) {
                log.debug("Building children for category ID {} with parent ID {}", category.getCategoryId(), parentId);
                List<CategoryDTO> children = buildNestedCategories(categoryDTOs, category.getCategoryId());
                if (!children.isEmpty()) {
                    category.setChildren(children);
                    log.debug("Found {} children for category ID {}", children.size(), category.getCategoryId());
                }
                nestedCategories.add(category);
            }
        }
        return nestedCategories;
    }
}