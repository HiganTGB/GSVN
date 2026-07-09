package com.gsvn.inventoryservice.mapper;

import com.gsvn.inventoryservice.model.dto.InventoryDTO;
import com.gsvn.inventoryservice.model.dto.SkuSellableDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InventoryMapper {

    List<InventoryDTO> findInventoryPage(
            @Param("warehouseId") Integer warehouseId,
            @Param("skuId") Integer skuId,
            @Param("limit") int limit,
            @Param("offset") int offset,
            @Param("sortField") String sortField,
            @Param("sortDirection") String sortDirection
    );

    long countLocalStock(
            @Param("warehouseId") Integer warehouseId,
            @Param("skuId") Integer skuId
    );

    long countGlobalStock(
            @Param("skuId") Integer skuId
    );
    boolean hasAnyPhysicalStock(@Param("skuIds") List<Long> skuIds);

    boolean hasPreorderSlot(@Param("skuIds") List<Long> skuIds);

    List<SkuSellableDTO> checkSkusSellable(List<Long> skuIds);


    int decreaseReservedGlobal(@Param("skuCode") String skuCode, @Param("quantity") Integer quantity);

    int increaseReservedLocal(@Param("skuCode") String skuCode, @Param("warehouseId") Integer warehouseId, @Param("quantity") Integer quantity);

    int decreasePhysicalAndReservedLocal(@Param("skuCode") String skuCode, @Param("warehouseId") Integer warehouseId, @Param("quantity") Integer quantity);


}