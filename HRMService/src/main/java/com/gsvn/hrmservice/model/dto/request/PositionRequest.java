package com.gsvn.hrmservice.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionRequest {

    @NotBlank(message = "POSITION_NAME_REQUIRED")
    @Size(max = 100, message = "POSITION_NAME_TOO_LONG")
    private String positionName;

    @DecimalMin(value = "0.0", message = "SALARY_MUST_BE_POSITIVE")
    private BigDecimal defaultBaseSalary = BigDecimal.valueOf(0);

    @Size(max = 500, message = "DESCRIPTION_TOO_LONG")
    private String description = "";
}