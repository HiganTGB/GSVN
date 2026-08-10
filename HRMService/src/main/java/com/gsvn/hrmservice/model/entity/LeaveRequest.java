package com.gsvn.hrmservice.model.entity;


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
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {
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
    private OffsetDateTime approvedAt;
    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
