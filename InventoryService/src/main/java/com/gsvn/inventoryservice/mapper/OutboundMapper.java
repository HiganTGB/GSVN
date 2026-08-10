package com.gsvn.inventoryservice.mapper;

import com.gsvn.inventoryservice.model.entity.OutboundItem;
import com.gsvn.inventoryservice.model.entity.OutboundReceipt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OutboundMapper {
    int insertReceipt(OutboundReceipt receipt);
    int insertBatchItems(@Param("items") List<OutboundItem> items);

    List<OutboundReceipt> findOutboundPage(
            @Param("warehouseId") Integer warehouseId,
            @Param("type") String type,
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    long countOutbound(
            @Param("warehouseId") Integer warehouseId,
            @Param("type") String type,
            @Param("keyword") String keyword
    );

    OutboundReceipt findById(@Param("id") Long id);
    List<OutboundItem> findItemsByOutboundId(@Param("outboundId") Long outboundId);
}