package com.tgb.gsvnbackend.model.mapper;

import com.tgb.gsvnbackend.model.dto.CategoryDTO;
import com.tgb.gsvnbackend.model.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDTO(Category category);

    Category toEntity(CategoryDTO categoryDTO);
}
