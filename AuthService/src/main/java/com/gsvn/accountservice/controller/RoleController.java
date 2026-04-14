package com.gsvn.accountservice.controller;

import com.gsvn.accountservice.common.ApiResponse;
import com.gsvn.accountservice.common.PageResponse;
import com.gsvn.accountservice.model.dto.request.RoleRequest;
import com.gsvn.accountservice.model.dto.response.PermissionResponse;
import com.gsvn.accountservice.model.dto.response.RoleResponse;
import com.gsvn.accountservice.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> createRole(@RequestBody @Valid RoleRequest request) {
        return new ApiResponse<>(roleService.create(request));
    }
    @PutMapping("/{roleId}")
    ApiResponse<RoleResponse> updateRole(@RequestBody @Valid RoleRequest request, @PathVariable Integer roleId) {
        return new ApiResponse<>(roleService.update(request,roleId));
    }
    @GetMapping
    ApiResponse<List<RoleResponse>> getRoles() {
        return new ApiResponse<>(roleService.getRoles());
    }
    @DeleteMapping("/{roleId}")
    ApiResponse<Void> deleteRole(@PathVariable Integer roleId) {
        roleService.delete(roleId);
        return new ApiResponse<>();
    }

    @GetMapping("/{roleId}")
    ApiResponse<Set<PermissionResponse>> getPermissionRole(@PathVariable Integer roleId) {
        return new ApiResponse<>(roleService.getRolePermissions(roleId));
    }
    @GetMapping("/search")
    public ApiResponse<PageResponse<RoleResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return new ApiResponse<>(roleService.searchRoles(
                keyword,
                sortBy,
                direction,
                page,
                size
        ));
    }
    @GetMapping("/internal/{roleId}")
    ApiResponse<Set<String>> getPermissionRoleInternal(@PathVariable Integer roleId) {
        return new ApiResponse<>(roleService.getRolePermissionStrings(roleId));
    }
}