package com.gsvn.inventoryservice.mapper;

import com.gsvn.inventoryservice.model.entity.Warehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WarehouseMapper {
    int insert(Warehouse warehouse);
    int update(Warehouse warehouse);
    Warehouse findById(@Param("id") Integer id);
    Warehouse findByCode(@Param("code") String code);
    List<Warehouse> findAllPaged(@Param("keyword") String keyword,
                                 @Param("sortField") String sortField,
                                 @Param("sortOrder") String sortOrder,
                                 @Param("limit") int limit,
                                 @Param("offset") int offset);
    long countAll(@Param("keyword") String keyword);
    List<Warehouse> findAll();
}