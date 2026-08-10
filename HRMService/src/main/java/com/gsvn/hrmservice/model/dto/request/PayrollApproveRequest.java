package com.gsvn.hrmservice.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayrollApproveRequest {

    @NotNull(message = "BONUS_REQUIRED")
    @DecimalMin(value = "0.0", message = "TOTAL_BONUS_MUST_BE_POSITIVE")
    private BigDecimal totalBonus;

    @NotNull(message = "DEDUCTION_REQUIRED")
    @DecimalMin(value = "0.0", message = "TOTAL_DEDUCTION_MUST_BE_POSITIVE")
    private BigDecimal totalDeduction;

    private String note;

    @NotNull(message = "ACCEPTANCE_STATUS_REQUIRED")
    private Boolean isAccepted;
}