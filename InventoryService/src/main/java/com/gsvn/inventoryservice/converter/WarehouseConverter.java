package com.gsvn.inventoryservice.converter;

import com.gsvn.inventoryservice.model.dto.request.WarehouseRequest;
import com.gsvn.inventoryservice.model.dto.response.WarehouseResponse;
import com.gsvn.inventoryservice.model.entity.Warehouse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WarehouseConverter {

    public Warehouse toEntity(WarehouseRequest request) {
        if (request == null) return null;

        return Warehouse.builder()
                .name(request.getName())
                .code(request.getCode())
                .staffId(request.getStaffId())
                .isActive(request.getIsActive() != null ? request.getIsActive() : false)
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .addressDetail(request.getAddressDetail())
                .provinceCode(request.getProvinceCode())
                .wardCode(request.getWardCode())
                .build();
    }

    public WarehouseResponse toResponse(Warehouse entity) {
        if (entity == null) return null;

        return WarehouseResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .staffId(entity.getStaffId())
                .isActive(entity.getIsActive())
                .contactName(entity.getContactName())
                .contactPhone(entity.getContactPhone())
                .addressDetail(entity.getAddressDetail())
                .provinceCode(entity.getProvinceCode())
                .wardCode(entity.getWardCode())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<WarehouseResponse> toResponseList(List<Warehouse> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntity(Warehouse entity, WarehouseRequest request) {
        if (request == null || entity == null) return;

        entity.setName(request.getName());
        entity.setCode(request.getCode());
        entity.setIsActive(request.getIsActive());
        entity.setContactName(request.getContactName());
        entity.setContactPhone(request.getContactPhone());
        entity.setAddressDetail(request.getAddressDetail());
        entity.setProvinceCode(request.getProvinceCode());
        entity.setWardCode(request.getWardCode());
    }
}