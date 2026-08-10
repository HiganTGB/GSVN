package com.gsvn.hrmservice.converter;

import com.gsvn.hrmservice.common.IBaseConverter;
import com.gsvn.hrmservice.model.dto.request.LeaveRequestRequest;
import com.gsvn.hrmservice.model.dto.response.LeaveRequestResponse;
import com.gsvn.hrmservice.model.entity.LeaveRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LeaveRequestConverter implements IBaseConverter<LeaveRequest, LeaveRequestRequest, LeaveRequestResponse> {

    public LeaveRequest toEntity(LeaveRequestRequest dto) {
        if (dto == null) return null;

        return LeaveRequest.builder()
                .leaveType(dto.getLeaveType())
                .reason(dto.getReason())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .effectiveDate(dto.getEffectiveDate())
                .build();
    }


    public LeaveRequestResponse toResponse(LeaveRequest entity) {
        if (entity == null) return null;

        LeaveRequestResponse dto = new LeaveRequestResponse();
        dto.setId(entity.getId());
        dto.setStaffId(entity.getStaffId());
        dto.setStaffName(entity.getStaffName());
        dto.setLeaveType(entity.getLeaveType());
        dto.setReason(entity.getReason());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setEffectiveDate(entity.getEffectiveDate());
        dto.setStatus(entity.getStatus());
        dto.setApprovedBy(entity.getApprovedBy());
        dto.setApprovedName(entity.getApprovedName());
        dto.setApprovedAt(entity.getApprovedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public List<LeaveRequestResponse> toResponseDTOList(List<LeaveRequest> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}