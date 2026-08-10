package com.gsvn.hrmservice.model.dto.response;

import com.gsvn.hrmservice.model.enums.LeaveType;
import com.gsvn.hrmservice.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestResponse {
    private Long id;
    private Long staffId;
    private String staffName;
    private LeaveType leaveType;
    private String reason;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate effectiveDate;
    private Status status;
    private Long approvedBy;
    private String approvedName;
    private String note;
    private OffsetDateTime approvedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public long getDurationDays() {
        if (startDate != null && endDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
        return 0;
    }
}