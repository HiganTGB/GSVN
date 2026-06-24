package com.gsvn.hrmservice.model.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    private Long staffId;
    private String userId;
    private String fullName;
    private String email;
    private LocalDate dob;
    private String gender;
    private String phoneNumber;
    private String address;
    private String identityCard;
    private String avatarUrl;
    private Integer warehouseId;
    private Integer positionId;
    private BigDecimal baseSalary;
    private OffsetDateTime deletedAt;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}