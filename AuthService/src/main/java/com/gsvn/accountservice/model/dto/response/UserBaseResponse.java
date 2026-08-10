package com.gsvn.accountservice.model.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserBaseResponse {
    String userId;
    String email;
    String userName;
    String phone;
    Boolean verifier;
    Boolean isActive;
    Boolean enabled;
    Boolean isStaff;
    Integer referenceId;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}