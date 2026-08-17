package com.gsvn.hrmservice.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BranchRequest {

    @NotBlank(message = "BRANCH_CODE_REQUIRED")
    @Size(max = 50, message = "BRANCH_CODE_TOO_LONG")
    private String branchCode;

    @NotBlank(message = "BRANCH_NAME_REQUIRED")
    @Size(max = 255, message = "BRANCH_NAME_TOO_LONG")
    private String branchName;

    @Size(max = 255, message = "ADDRESS_TOO_LONG")
    private String address;

    @Size(max = 20, message = "PHONE_NUMBER_TOO_LONG")
    private String phoneNumber;

    private Boolean isActive = true;
}