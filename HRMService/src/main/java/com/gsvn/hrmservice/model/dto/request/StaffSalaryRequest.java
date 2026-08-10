package com.gsvn.hrmservice.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class StaffSalaryRequest {

    @NotNull(message = "BASE_SALARY_REQUIRED")
    @DecimalMin(value = "0.0", inclusive = true, message = "BASE_SALARY_CANNOT_BE_NEGATIVE")
    private BigDecimal baseSalary;

    @NotNull(message = "POSITION_ID_REQUIRED")
    private Integer positionId;

    @Size(max = 500, message = "NOTE_TOO_LONG")
    private String note;
}