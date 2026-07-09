package com.gsvn.inventoryservice.converter;


import com.gsvn.inventoryservice.model.dto.request.SupplierRequest;
import com.gsvn.inventoryservice.model.dto.response.SupplierResponse;
import com.gsvn.inventoryservice.model.entity.Supplier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SupplierConverter {

    public Supplier toEntity(SupplierRequest request) {
        if (request == null) return null;

        return Supplier.builder()
                .name(request.getName())
                .contactName(request.getContactName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .taxCode(request.getTaxCode())
                .isActive(request.getIsActive() != null ? request.getIsActive() : false)
                .note(request.getNote())
                .build();
    }

    public SupplierResponse toResponse(Supplier entity) {
        if (entity == null) return null;

        return SupplierResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .contactName(entity.getContactName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .taxCode(entity.getTaxCode())
                .isActive(entity.getIsActive())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<SupplierResponse> toResponseList(List<Supplier> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntity(Supplier entity, SupplierRequest request) {
        if (request == null || entity == null) return;

        entity.setName(request.getName());
        entity.setContactName(request.getContactName());
        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setTaxCode(request.getTaxCode());
        entity.setIsActive(request.getIsActive());
        entity.setNote(request.getNote());
    }
}