package com.gsvn.inventoryservice.mapper;

import com.gsvn.inventoryservice.model.entity.SkuStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SkuStockMapper {
    SkuStock findByIdAndWarehouse(@Param("skuId") Long skuId, @Param("warehouseId") Integer warehouseId);
    SkuStock findByCode(@Param("skuCode") String skuCode, @Param("warehouseId") Integer warehouseId);
    List<SkuStock> findById(@Param("skuId") Long skuId);
    int upsertPhysicalStock(
            @Param("skuId") Long skuId,
            @Param("skuCode") String skuCode,
            @Param("warehouseId") Integer warehouseId,
            @Param("quantity") Integer quantity
    );

    int deductPhysicalStock(
            @Param("skuId") Long skuId,
            @Param("warehouseId") Integer warehouseId,
            @Param("quantity") Integer quantity
    );
    int incrementReservedStock(@Param("skuId") Long skuId,
                               @Param("warehouseId") Integer warehouseId,
                               @Param("quantity") Integer quantity);
    int decrementReservedStock(@Param("skuId") Long skuId,
                               @Param("warehouseId") Integer warehouseId,
                               @Param("quantity") Integer quantity);

}