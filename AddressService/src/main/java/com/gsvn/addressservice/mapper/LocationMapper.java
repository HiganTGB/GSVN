package com.gsvn.addressservice.mapper;

import com.gsvn.addressservice.model.entity.Province;
import com.gsvn.addressservice.model.entity.Ward;
import com.gsvn.addressservice.model.entity.WardPartner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LocationMapper {

    List<Province> findAllProvinces();

    List<Ward> findWardsByProvinceCode(@Param("provinceCode") String provinceCode);

    WardPartner findWardPartner(@Param("wardCode") String wardCode,
                                @Param("partnerName") String partnerName);
}