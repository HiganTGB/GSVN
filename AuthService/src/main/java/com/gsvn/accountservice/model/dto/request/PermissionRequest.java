package com.gsvn.accountservice.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PermissionRequest(
        @NotBlank(message = "PERMISSION_NAME_REQUIRED")
        @Size(min = 3, max = 50, message = "PERMISSION_NAME_INVALID_SIZE")
        String name, String description) {
}