package com.gsvn.hrmservice.model.dto.request;

import com.gsvn.hrmservice.model.enums.LeaveType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestRequest {

    @NotNull(message = "LEAVE_TYPE_REQUIRED")
    private LeaveType leaveType;

    @NotBlank(message = "REASON_REQUIRED")
    @Size(max = 500, message = "REASON_TOO_LONG")
    private String reason;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate effectiveDate;

    private boolean isEndDateValid() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !endDate.isBefore(startDate);
    }
}