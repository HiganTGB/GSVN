package com.gsvn.hrmservice.model.dto.request;

import com.gsvn.hrmservice.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveStatusApproveRequest {

    @NotNull(message = "STATUS_REQUIRED")
    private Status status;
    private String note;
}