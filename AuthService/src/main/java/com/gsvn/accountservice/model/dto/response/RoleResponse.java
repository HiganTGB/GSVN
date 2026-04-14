package com.gsvn.accountservice.model.dto.response;

import java.time.OffsetDateTime;
import java.util.Set;

public record RoleResponse(int roleId, String name,
                           String description,
                           Set<PermissionResponse> permissions, OffsetDateTime createdAt,OffsetDateTime updatedAt) {
}
