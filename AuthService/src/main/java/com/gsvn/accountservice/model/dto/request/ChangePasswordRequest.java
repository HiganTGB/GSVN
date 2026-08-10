package com.gsvn.accountservice.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "OLD_PASSWORD_REQUIRED")
        String oldPassword,

        @NotBlank(message = "NEW_PASSWORD_REQUIRED")
        @Size(min = 6, max = 50, message = "PASSWORD_INVALID_SIZE")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$",
                message = "PASSWORD_TOO_WEAK"
        )
        String newPassword,

        @NotBlank(message = "RE_PASSWORD_REQUIRED")
        String rePassword
) {
}