package com.gsvn.accountservice.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequest(
        @NotBlank(message = "EMAIL_REQUIRED")
        @Email(message = "INVALID_EMAIL")
        String email,
        @NotBlank(message = "PASSWORD_REQUIRED")
        @Size(min = 8, message = "PASSWORD_INVALID_SIZE")
        String password) {
}
