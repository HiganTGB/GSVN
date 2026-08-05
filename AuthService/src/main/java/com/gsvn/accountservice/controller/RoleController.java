package com.gsvn.accountservice.controller;

import com.gsvn.accountservice.common.ApiResponse;
import com.gsvn.accountservice.common.PageResponse;
import com.gsvn.accountservice.model.dto.request.RoleRequest;
import com.gsvn.accountservice.model.dto.response.PermissionResponse;
import com.gsvn.accountservice.model.dto.response.RoleResponse;
import com.gsvn.accountservice.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Operation(summary = "Create role", description = "Creates a new role with assigned permissions.")
    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('role_create')")
    ApiResponse<RoleResponse> createRole(@RequestBody @Valid RoleRequest request) {
        return new ApiResponse<>(roleService.create(request));
    }
    @PutMapping("/{roleId}")
    @Operation(summary = "Update role", description = "Updates an existing role details and permissions by role ID.")
    @PreAuthorize("hasAuthority('all') or hasAuthority('role_update')")
    ApiResponse<RoleResponse> updateRole(@RequestBody @Valid RoleRequest request, @PathVariable Integer roleId) {
        return new ApiResponse<>(roleService.update(request,roleId));
    }
    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieves a complete list of all roles.")
    ApiResponse<List<RoleResponse>> getRoles() {
        return new ApiResponse<>(roleService.getRoles());
    }
    @DeleteMapping("/{roleId}")
    @Operation(summary = "Delete role", description = "Deletes a role by its ID.")
    @PreAuthorize("hasAuthority('all') or hasAuthority('role_delete')")
    ApiResponse<Void> deleteRole(@PathVariable Integer roleId) {
        roleService.delete(roleId);
        return new ApiResponse<>();
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "Get permissions by role ID", description = "Retrieves details of all permissions assigned to a specific role.")
    ApiResponse<Set<PermissionResponse>> getPermissionRole(@PathVariable Integer roleId) {
        return new ApiResponse<>(roleService.getRolePermissions(roleId));
    }
    @GetMapping("/search")
    @Operation(summary = "Search roles with pagination", description = "Filters roles by keyword and returns a paginated list with dynamic sorting.")
    @PreAuthorize("hasAuthority('all') or hasAuthority('role_read')")
    public ApiResponse<PageResponse<RoleResponse>> search(
            @Parameter(description = "Keyword to filter roles by name or description")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Field name to sort results by")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sorting direction: 'asc' or 'desc'")
            @RequestParam(defaultValue = "desc") String direction,

            @Parameter(description = "Page number (1-based index)")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "Number of items per page")
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
    @Operation(summary = "Get permission codes by role ID (Internal)", description = "Retrieves permission string codes")
    ApiResponse<Set<String>> getPermissionRoleInternal(@PathVariable Integer roleId) {
        return new ApiResponse<>(roleService.getRolePermissionStrings(roleId));
    }
}