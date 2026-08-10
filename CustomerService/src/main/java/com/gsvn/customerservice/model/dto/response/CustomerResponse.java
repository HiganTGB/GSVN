package com.gsvn.customerservice.model.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {
    private Long customerId;
    private String userId;
    private String email;
    private String fullName;
    private String gender;
    private LocalDate dob;
    private String phoneNumber;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}