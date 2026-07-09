package com.gsvn.inventoryservice.mapper;

import com.gsvn.inventoryservice.model.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SupplierMapper {

    Optional<Supplier> findById(Integer id);

    List<Supplier> findAll();

    int insert(Supplier supplier);

    int update(Supplier supplier);

    int delete(Integer id);

    List<Supplier> findPage(@Param("keyword") String keyword,
                            @Param("isActive") Boolean isActive,
                            @Param("sortField") String sortField,
                            @Param("sortOrder") String sortOrder,
                            @Param("limit") int limit,
                            @Param("offset") int offset);

    long countSearch(@Param("keyword") String keyword,
                     @Param("isActive") Boolean isActive);
}