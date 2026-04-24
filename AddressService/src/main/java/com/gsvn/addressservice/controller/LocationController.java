package com.gsvn.addressservice.controller;

import com.gsvn.addressservice.model.dto.response.ApiResponse;
import com.gsvn.addressservice.model.entity.Province;
import com.gsvn.addressservice.model.entity.Ward;
import com.gsvn.addressservice.model.entity.WardPartner;
import com.gsvn.addressservice.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/provinces")
    public ApiResponse<List<Province>> getProvinces() {
        return new ApiResponse<>(locationService.getAllProvinces());
    }

    @GetMapping("/provinces/{code}/wards")
    public ApiResponse<List<Ward>> getWards(@PathVariable String code) {
        return new ApiResponse<>(locationService.getWardsByProvince(code));
    }
    @GetMapping("/partner-mapping")
    public ApiResponse<WardPartner> getPartnerMapping(
            @RequestParam String wardCode,
            @RequestParam String partnerName) {
        WardPartner partner = locationService.getPartnerMapping(wardCode, partnerName);
        return partner != null ? new ApiResponse<>(partner) : new ApiResponse<>(null);
    }
}