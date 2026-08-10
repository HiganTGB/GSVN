package com.gsvn.hrmservice.model.entity;

import com.gsvn.hrmservice.model.enums.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payroll {
    private Long id;
    private Long staffId;
    private String staffName;
    private String salaryPeriod;
    private Integer positionId;
    private String positionName;
    private BigDecimal baseSalary;
    private Double workingDays;
    private BigDecimal totalBonus;
    private BigDecimal totalDeduction;
    private BigDecimal finalSalary;
    private Long approvedBy;
    private String approvedName;
    private OffsetDateTime approvedAt;
    private OffsetDateTime paidAt;
    private PayrollStatus status;
    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}