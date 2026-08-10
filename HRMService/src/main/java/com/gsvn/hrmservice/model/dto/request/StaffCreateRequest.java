package com.gsvn.hrmservice.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record StaffCreateRequest(
        @Valid
        @NotNull(message = "STAFF_INFO_REQUIRED")
        StaffRequest staffRequest,

        @Valid
        @NotNull(message = "SALARY_INFO_REQUIRED")
        StaffSalaryRequest salaryRequest,
        @NotNull(message = "CREATE_ACCOUNT_OPTION_REQUIRED")
        Boolean createAccount
) {
}