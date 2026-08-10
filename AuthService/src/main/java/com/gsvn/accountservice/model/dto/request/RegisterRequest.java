package com.gsvn.accountservice.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "INVALID_EMAIL_FORMAT")
    private  String email;

    @NotBlank(message = "FULL_NAME_REQUIRED")
    @Size(max = 100, message = "NAME_TOO_LONG")
    private String fullName;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, message = "PASSWORD_TOO_SHORT")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$",
            message = "PASSWORD_TOO_WEAK"
    )
    private  String password;

    @Pattern(regexp = "^\\d{10}$", message = "INVALID_PHONE_NUMBER")
    private  String phoneNumber;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "INVALID_GENDER")
    private String gender;

    @Past(message = "DOB_MUST_BE_IN_PAST")
    private LocalDate dob;
}
