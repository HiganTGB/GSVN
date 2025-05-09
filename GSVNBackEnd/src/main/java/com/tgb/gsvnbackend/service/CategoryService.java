package com.tgb.gsvnbackend.service;


import com.tgb.gsvnbackend.model.dto.CategoryDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    CategoryDTO read(int id);

    List<CategoryDTO> readAll();
    CategoryDTO create(CategoryDTO categoryDTO);

    CategoryDTO update(int id, CategoryDTO categoryDTO);
    Page<CategoryDTO> readByPage(int page, int size, String sortBy, String sortDirection);
    void delete(int id);
    List<CategoryDTO> getCategoriesNested();
}
