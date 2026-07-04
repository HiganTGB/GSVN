package com.gsvn.productservice.mapper;
import com.gsvn.productservice.model.entity.Brand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BrandMapper {
    Brand findById(@Param("id") Integer id);
    List<Brand> findAll();
    int insert(Brand brand);
    int update(Brand brand);
    int delete(@Param("id") Integer id);
    List<Brand> findPage(@Param("keyword") String keyword,
                         @Param("offset") int offset,
                         @Param("limit") int limit,
                         @Param("sortField") String sortField,
                         @Param("sortOrder") String sortOrder);

    long countSearch(@Param("keyword") String keyword);


    boolean existsById(@Param("id") Integer id);
    boolean existsByName(@Param("name") String name, @Param("excludeId") Integer excludeId);
}