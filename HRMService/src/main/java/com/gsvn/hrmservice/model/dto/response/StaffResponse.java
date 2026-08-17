package com.gsvn.hrmservice.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class StaffResponse {
    private Long staffId;
    private String fullName;
    private String email;
    private LocalDate dob;
    private String gender;
    private String phoneNumber;
    private String address;
    private String avatarUrl;
    private String identityCard;

    private Integer branchId;
    private String branchName;

    private Integer positionId;
    private String positionName;
    private BigDecimal baseSalary;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}