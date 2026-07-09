package com.gsvn.inventoryservice.mapper;

import com.gsvn.inventoryservice.model.entity.InboundItem;
import com.gsvn.inventoryservice.model.entity.InboundReceipt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InboundMapper {
    int insertReceipt(InboundReceipt receipt);
    int insertBatchItems(@Param("items") List<InboundItem> items);

    List<InboundReceipt> findInboundPage(
            @Param("warehouseId") Integer warehouseId,
            @Param("supplierId") Integer supplierId,
            @Param("type") String type,
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    long countInbound(
            @Param("warehouseId") Integer warehouseId,
            @Param("supplierId") Integer supplierId,
            @Param("type") String type,
            @Param("keyword") String keyword
    );

    InboundReceipt findById(@Param("id") Long id);
    List<InboundItem> findItemsByInboundId(@Param("inboundId") Long inboundId);
}