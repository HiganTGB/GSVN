package com.gsvn.accountservice.mapper;

import com.gsvn.accountservice.model.dto.request.PermissionRequest;
import com.gsvn.accountservice.model.dto.response.PermissionResponse;
import com.gsvn.accountservice.model.entity.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {
    public Permission toPermission(PermissionRequest request) {
        if (request == null) {
            return null;
        }
        return Permission.builder()
                .permissionName(request.name())
                .description(request.description())
                .build();
    }

    public PermissionResponse toPermissionResponse(Permission permission) {
        return TO_PERMISSION_RESPONSE(permission);
    }

    public static PermissionResponse TO_PERMISSION_RESPONSE(Permission permission)
    {
        if (permission == null) {
            return null;
        }
        return new PermissionResponse(permission.getPermissionId(),permission.getPermissionName(),permission.getDescription());
    }
}
