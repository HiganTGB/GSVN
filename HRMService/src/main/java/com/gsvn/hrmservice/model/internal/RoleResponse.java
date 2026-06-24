package com.gsvn.hrmservice.model.internal;

import com.gsvn.hrmservice.model.internal.PermissionResponse;

import java.time.OffsetDateTime;
import java.util.Set;

public record RoleResponse(int roleId, String name,
                           String description,
                           Set<PermissionResponse> permissions, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
}
