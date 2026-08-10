package com.gsvn.hrmservice.model.entity;

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
public class StaffSalary {
    private Long id;
    private Long staffId;
    private Integer positionId;
    private String positionName;
    private BigDecimal baseSalary;
    private OffsetDateTime effectiveDate;
    private String note;
    private OffsetDateTime createdAt;
}