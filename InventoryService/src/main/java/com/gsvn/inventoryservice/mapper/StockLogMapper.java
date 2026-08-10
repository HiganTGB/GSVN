package com.gsvn.inventoryservice.mapper;

import com.gsvn.inventoryservice.model.entity.StockLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockLogMapper {

    int insertLog(StockLog log);

    List<StockLog> findLogPage(
            @Param("skuId") Integer skuId,
            @Param("warehouseId") Integer warehouseId,
            @Param("type") String type,
            @Param("referenceId") String referenceId,
            @Param("sagaId") java.util.UUID sagaId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    long countLogs(
            @Param("skuId") Integer skuId,
            @Param("warehouseId") Integer warehouseId,
            @Param("type") String type
    );
}