package com.gsvn.accountservice.model.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RoleRequest(
        @NotBlank(message = "ROLE_NAME_REQUIRED")
        @Size(min = 3, max = 50, message = "ROLE_NAME_INVALID_SIZE")
        String roleName,

        @NotBlank(message = "DESCRIPTION_REQUIRED")
        String description,

        @NotNull(message = "PERMISSION_SET_CANNOT_BE_NULL")
        Set<Integer> permissionId
) {
}