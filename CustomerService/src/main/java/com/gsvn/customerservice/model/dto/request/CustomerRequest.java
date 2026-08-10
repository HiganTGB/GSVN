package com.gsvn.customerservice.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

    @Data
    public class CustomerRequest {

        @NotBlank(message = "FULL_NAME_REQUIRED")
        @Size(max = 100, message = "NAME_TOO_LONG")
        private String fullName;

        @NotBlank(message = "EMAIL_REQUIRED")
        @Email(message = "INVALID_EMAIL_FORMAT")
        private String email;

        @NotBlank(message = "PHONE_NUMBER_REQUIRED")
        @Pattern(regexp = "^[0-9]{10}$", message = "PHONE_NUMBER_INVALID_FORMAT")
        private String phoneNumber;

        @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "INVALID_GENDER")
        private String gender;

        @Past(message = "DOB_MUST_BE_IN_PAST")
        private LocalDate dob;
    }