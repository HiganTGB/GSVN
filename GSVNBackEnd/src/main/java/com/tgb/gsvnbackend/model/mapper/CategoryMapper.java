package com.tgb.gsvnbackend.model.mapper;

import com.tgb.gsvnbackend.model.dto.CategoryDTO;
import com.tgb.gsvnbackend.model.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "children",ignore = true)
    CategoryDTO toDTO(Category category);
    @Mapping(target = "category_id", ignore = true)

    Category toEntity(CategoryDTO categoryDTO);
}
