package com.gsvn.customerservice.model.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class Customer {
    private Long customerId;
    private String userId;
    private String email;
    private String fullName;
    private String gender;
    private LocalDate dob;
    private String phoneNumber;
    private OffsetDateTime deletedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}