package com.gsvn.hrmservice.model.dto.response;

import com.gsvn.hrmservice.model.enums.PayrollStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Data
public class PayrollResponse {
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
