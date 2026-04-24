package com.gsvn.addressservice.service;

import com.gsvn.addressservice.model.entity.Province;
import com.gsvn.addressservice.model.entity.Ward;
import com.gsvn.addressservice.model.entity.WardPartner;

import java.util.List;

public interface LocationService {
    List<Province> getAllProvinces();
    List<Ward> getWardsByProvince(String provinceCode);
    WardPartner getPartnerMapping(String wardCode, String partnerName);
}