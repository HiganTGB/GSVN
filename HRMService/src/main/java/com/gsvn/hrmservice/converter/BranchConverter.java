package com.gsvn.hrmservice.converter;

import com.gsvn.hrmservice.common.IBaseConverter;
import com.gsvn.hrmservice.model.dto.request.BranchRequest;
import com.gsvn.hrmservice.model.dto.response.BranchResponse;
import com.gsvn.hrmservice.model.entity.Branch;
import org.springframework.stereotype.Component;

@Component
public class BranchConverter implements IBaseConverter<Branch, BranchRequest, BranchResponse> {

    @Override
    public Branch toEntity(BranchRequest request) {
        if (request == null) return null;

        return Branch.builder()
                .branchCode(request.getBranchCode())
                .branchName(request.getBranchName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .isActive(request.getIsActive())
                .build();
    }

    @Override
    public BranchResponse toResponse(Branch entity) {
        if (entity == null) return null;

        return BranchResponse.builder()
                .branchId(entity.getBranchId())
                .branchCode(entity.getBranchCode())
                .branchName(entity.getBranchName())
                .address(entity.getAddress())
                .phoneNumber(entity.getPhoneNumber())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Branch updateEntity(Branch entity, BranchRequest request) {
        if (entity == null || request == null) return entity;

        entity.setBranchCode(request.getBranchCode());
        entity.setBranchName(request.getBranchName());
        entity.setAddress(request.getAddress());
        entity.setPhoneNumber(request.getPhoneNumber());
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }
        return entity;
    }
}