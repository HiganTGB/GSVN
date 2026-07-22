package com.gsvn.shipmentservice.mapper;


import com.gsvn.shipmentservice.model.entity.WarehousePartner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface WarehousePartnerMapper {


    int save(WarehousePartner record);

    List<WarehousePartner> selectByWarehouseCode(@Param("warehouseCode") String warehouseCode);

    Optional<WarehousePartner> selectById(@Param("id") Integer id);

    Optional<WarehousePartner> selectFirstByWarehouseAndPartner(
            @Param("warehouseCode") String warehouseCode,
            @Param("partnerName") String partnerName
    );
    int deleteByWarehouseAndPartner(
            @Param("warehouseCode") String warehouseCode,
            @Param("partnerName") String partnerName
    );
}