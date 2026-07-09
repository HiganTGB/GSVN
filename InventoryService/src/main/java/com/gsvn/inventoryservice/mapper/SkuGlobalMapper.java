package com.gsvn.inventoryservice.mapper;

import com.gsvn.inventoryservice.model.entity.SkuGlobal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SkuGlobalMapper {


    Optional<SkuGlobal> findBySkuId(@Param("skuId") Long skuId);
    Optional<SkuGlobal> findBySkuCode(@Param("skuCode") String skuCode);
    List<SkuGlobal> findBySkuIds(@Param("skuIds") List<Long> skuIds);


    int upsertSkuGlobal(SkuGlobal skuGlobal);

    int updatePreOrderSettings(
            @Param("skuId") Long skuId,
            @Param("preLimitQuantity") Integer preLimitQuantity
    );

    int updateReservedQuantity(@Param("skuCode") String skuCode, @Param("quantity") Integer quantity);

    int incrementPreOrderCount(
            @Param("skuCode") String skuCode,
            @Param("quantity") Integer quantity
    );
    int decrementPreOrderCount(
            @Param("skuCode") String skuCode,
            @Param("quantity") Integer quantity
    );
    boolean existsBySkuId(@Param("skuId") Long skuId);

    boolean existsById(@Param("skuId") Long skuId);
    List<Long> findAllExistedIds(@Param("skuIds") List<Long> skuIds);
}