package com.gsvn.hrmservice.converter;


import com.gsvn.hrmservice.common.IBaseConverter;
import com.gsvn.hrmservice.model.dto.request.PositionRequest;

import com.gsvn.hrmservice.model.dto.response.PositionResponse;

import com.gsvn.hrmservice.model.entity.Position;

import org.springframework.stereotype.Component;

@Component
public class PositionConverter implements IBaseConverter<Position, PositionRequest, PositionResponse> {

    public Position toEntity(PositionRequest request) {
        if (request == null) return null;

        return Position.builder()
                .positionName(request.getPositionName())
                .defaultBaseSalary(request.getDefaultBaseSalary())
                .description(request.getDescription())
                .build();
    }


    public PositionResponse toResponse(Position entity) {
        if (entity == null) return null;

        return PositionResponse.builder()
                .positionId(entity.getPositionId())
                .positionName(entity.getPositionName())
                .defaultBaseSalary(entity.getDefaultBaseSalary())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }


    public Position updateEntity(Position entity, PositionRequest request) {
        entity.setPositionName(request.getPositionName());
        entity.setDescription(request.getDescription());
        entity.setDefaultBaseSalary(request.getDefaultBaseSalary());
        return entity;
    }
}