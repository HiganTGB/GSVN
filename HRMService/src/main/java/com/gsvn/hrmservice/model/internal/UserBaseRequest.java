package com.gsvn.hrmservice.model.internal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserBaseRequest {

    @Email(message = "Invalid email format")
    String email;

    @NotBlank(message = "Username must not be blank")
    String userName;

    String password;

    String phoneNumber;
    Boolean verifier = false;
}