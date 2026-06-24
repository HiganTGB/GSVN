package com.gsvn.hrmservice.converter;


import com.gsvn.hrmservice.model.dto.response.PayrollResponse;
import com.gsvn.hrmservice.model.entity.Payroll;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PayrollConverter {

    public PayrollResponse toResponse(Payroll entity) {
        if (entity == null) return null;

        PayrollResponse dto = new PayrollResponse();
        dto.setId(entity.getId());
        dto.setStaffId(entity.getStaffId());
        dto.setStaffName(entity.getStaffName());
        dto.setSalaryPeriod(entity.getSalaryPeriod());
        dto.setPositionId(entity.getPositionId());
        dto.setPositionName(entity.getPositionName());
        dto.setBaseSalary(entity.getBaseSalary());
        dto.setWorkingDays(entity.getWorkingDays());
        dto.setTotalBonus(entity.getTotalBonus());
        dto.setTotalDeduction(entity.getTotalDeduction());
        dto.setFinalSalary(entity.getFinalSalary());
        dto.setApprovedBy(entity.getApprovedBy());
        dto.setApprovedName(entity.getApprovedName());
        dto.setApprovedAt(entity.getApprovedAt());
        dto.setPaidAt(entity.getPaidAt());
        dto.setStatus(entity.getStatus());
        dto.setNote(entity.getNote());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public List<PayrollResponse> toResponseDTOList(List<Payroll> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}