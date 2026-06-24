package com.gsvn.hrmservice.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PayrollReportItem {
    private Integer stt;
    private Long staffId;
    private String staffName;
    private BigDecimal baseSalary;
    private BigDecimal totalBonus;
    private BigDecimal totalDeduction;
    private BigDecimal finalSalary;
}