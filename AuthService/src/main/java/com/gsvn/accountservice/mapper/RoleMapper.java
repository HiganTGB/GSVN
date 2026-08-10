package com.gsvn.accountservice.mapper;

import com.gsvn.accountservice.model.dto.request.RoleRequest;
import com.gsvn.accountservice.model.dto.response.RoleResponse;
import com.gsvn.accountservice.model.entity.Permission;
import com.gsvn.accountservice.model.entity.Role;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleMapper {
    public static Role TO_ROLE(RoleRequest request) {
        if (request == null) {
            return null;
        }
        return Role.builder()
                .roleName(request.roleName())
                .description(request.description())
                .build();
    }
    public static RoleResponse TO_ROLE_RESPONSE(Role role,Set<Permission> permissions) {
        if (role == null) {
            return null;
        }
        if (permissions == null) {
            return new RoleResponse(role.getRoleId(), role.getRoleName(), role.getDescription(), Collections.emptySet(),role.getCreatedAt(),role.getUpdatedAt());
        }
        return new RoleResponse(role.getRoleId(), role.getRoleName(), role.getDescription(), permissions.stream().map(PermissionMapper::TO_PERMISSION_RESPONSE).collect(Collectors.toSet()),role.getCreatedAt(),role.getUpdatedAt());
    }

}
