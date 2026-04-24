package com.gsvn.addressservice.service.impl;

import com.gsvn.addressservice.mapper.LocationMapper;
import com.gsvn.addressservice.model.entity.Province;
import com.gsvn.addressservice.model.entity.Ward;
import com.gsvn.addressservice.model.entity.WardPartner;
import com.gsvn.addressservice.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {
    @Autowired
    private final LocationMapper locationMapper;

    public LocationServiceImpl(LocationMapper locationMapper) {
        this.locationMapper = locationMapper;
    }


    @Override
    public List<Province> getAllProvinces() {
        return locationMapper.findAllProvinces();
    }

    @Override
    public List<Ward> getWardsByProvince(String provinceCode) {
        if (provinceCode == null || provinceCode.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return locationMapper.findWardsByProvinceCode(provinceCode);
    }

    @Override
    public WardPartner getPartnerMapping(String wardCode, String partnerName) {
        if (wardCode == null || partnerName == null) {
            return null;
        }
        return locationMapper.findWardPartner(wardCode, partnerName);
    }
}