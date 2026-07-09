package com.gsvn.inventoryservice.model.internal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffResponse {
    private Integer staffId;
    private String fullName;
    private String email;
    private LocalDate dob;
    private String gender;
    private String phoneNumber;
    private String address;
    private String avatarUrl;
    private String identityCard;
    private Integer warehouseId;
    private Integer positionId;
    private String  positionName;
    private BigDecimal baseSalary;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}