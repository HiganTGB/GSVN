package com.gsvn.hrmservice.converter;

import com.gsvn.hrmservice.common.IBaseConverter;
import com.gsvn.hrmservice.model.dto.request.StaffSalaryRequest;
import com.gsvn.hrmservice.model.dto.response.StaffSalaryResponse;
import com.gsvn.hrmservice.model.entity.StaffSalary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StaffSalaryConverter  {


    public StaffSalary toEntity(StaffSalaryRequest request,Long staffId) {
        if (request == null) return null;

        return StaffSalary.builder()
                .staffId(staffId)
                .baseSalary(request.getBaseSalary())
                .positionId(request.getPositionId())
                .note(request.getNote())
                .build();
    }


    public StaffSalaryResponse toResponse(StaffSalary entity) {
        if (entity == null) return null;

        return StaffSalaryResponse.builder()
                .id(entity.getId())
                .staffId(entity.getStaffId())
                .baseSalary(entity.getBaseSalary())
                .positionId(entity.getPositionId())
                .positionName(entity.getPositionName())
                .effectiveDate(entity.getEffectiveDate())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .build();
    }
    public List<StaffSalaryResponse> toResponseList(List<StaffSalary> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}