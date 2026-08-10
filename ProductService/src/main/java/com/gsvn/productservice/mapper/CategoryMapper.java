package com.gsvn.productservice.mapper;

import com.gsvn.productservice.model.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryMapper {
    Category findById(@Param("id") Integer id);


    List<Category> findRootCategories();

    Category findWithChildren(@Param("id") Integer id);

    int insert(Category category);
    int update(Category category);
    List<Category> findPage(@Param("keyword") String keyword,
                         @Param("offset") int offset,
                         @Param("limit") int limit);

    long countSearch(@Param("keyword") String keyword);

    List<Category> findAll();

    boolean existsById(@Param("id") Integer id);
    boolean existsByName(@Param("name") String name, @Param("excludeId") Integer excludeId);
}