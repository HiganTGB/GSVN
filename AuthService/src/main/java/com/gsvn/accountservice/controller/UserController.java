package com.gsvn.accountservice.controller;

import com.gsvn.accountservice.common.ApiResponse;

import com.gsvn.accountservice.common.PageResponse;
import com.gsvn.accountservice.model.dto.request.ChangePasswordRequest;
import com.gsvn.accountservice.model.dto.request.SyncUserRequest;
import com.gsvn.accountservice.model.dto.request.UserBaseRequest;

import com.gsvn.accountservice.model.dto.response.RoleResponse;
import com.gsvn.accountservice.model.dto.response.UserBaseResponse;

import com.gsvn.accountservice.service.RoleService;
import com.gsvn.accountservice.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "User Management", description = "Endpoints for managing user profiles, status, roles, permissions, and internal integrations")
public class UserController {
    UserService userService;
    RoleService roleService;

    @GetMapping("/my-info")
    @Operation(summary = "Get current user info", description = "Retrieves profile information for the currently authenticated user.")
    ApiResponse<UserBaseResponse> getMyInfo() {
        return new ApiResponse<>(userService.getMyInfo());
    }

    @GetMapping("/my-role")
    @Operation(summary = "Get current user role", description = "Retrieves the primary role assigned to the currently authenticated user.")
    ApiResponse<String> getMyRoles() {
        return new ApiResponse<>(roleService.getMyRoles());
    }

    @GetMapping("/my-permissions")
    @Operation(summary = "Get current user permissions", description = "Retrieves the full set of permission codes for the currently authenticated user.")
    ApiResponse<Set<String>> getMyPermissions() {
        return new ApiResponse<>(roleService.getMyPermissions());
    }

    @PostMapping("/internal/create")
    @Operation(summary = "Create user (Internal)", description = "Internal endpoint to provision a new user account.")
    ApiResponse<UserBaseResponse> create(@RequestBody @Valid UserBaseRequest request) {
        return new ApiResponse<>(userService.create(request,true));
    }

    @PostMapping("/internal/{user_id}/sync")
    @Operation(summary = "Sync user profile (Internal)", description = "Internal endpoint to synchronize user profile information across services.")
    ApiResponse<UserBaseResponse> sync(@PathVariable String user_id,@RequestBody @Valid SyncUserRequest request) {
        return new ApiResponse<>(userService.syncUser(user_id,request));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Allows an authenticated user to update their account password.")
    ApiResponse<Boolean> changePassword(@RequestBody @Valid ChangePasswordRequest request){
        return new ApiResponse<>(userService.changePassword(request));
    }

    @GetMapping("/{user_id}/roles")
    @Operation(summary = "Get roles by user ID", description = "Retrieves the set of roles assigned to a specific user.")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('user_read'))")
    ApiResponse<Set<RoleResponse>> getUserRole(@PathVariable String user_id){
        return new ApiResponse<>(roleService.getRolesByUserId(user_id));
    }

    @PutMapping("/{user_id}/roles")
    @Operation(summary = "Update user roles", description = "Replaces the assigned roles for a specific user with a new list of role IDs.")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('user_update'))")
    ApiResponse<Set<RoleResponse>> updateUserRole(@PathVariable String user_id,@RequestBody List<Integer> roleIds){
        return new ApiResponse<>(roleService.updateRolesByUserId(user_id,roleIds));
    }

    @PutMapping("/{user_id}/lock")
    @Operation(summary = "Lock user account", description = "Locks a user account to prevent login access.")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('user_update'))")
    ApiResponse<Boolean> lockUser(@PathVariable String user_id){
        userService.changeLockUser(user_id,false);
        return new ApiResponse<>(true);
    }

    @PutMapping("/internal/{user_id}/lock")
    @Operation(summary = "Lock user account (Internal)", description = "Internal service endpoint to lock a user account.")
    ApiResponse<Boolean> lockInternalUser(@PathVariable String user_id){
        userService.changeLockUser(user_id,false);
        return new ApiResponse<>(true);
    }

    @PutMapping("/{user_id}/unlock")
    @Operation(summary = "Unlock user account", description = "Unlocks a user account to restore access.")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('user_update'))")
    ApiResponse<Boolean> unlockUser(@PathVariable String user_id){
        userService.changeLockUser(user_id,true);
        return new ApiResponse<>(true);
    }

    @PutMapping("/internal/{user_id}/unlock")
    @Operation(summary = "Unlock user account (Internal)", description = "Internal service endpoint to unlock a user account.")
    ApiResponse<Boolean> unlockInternalUser(@PathVariable String user_id){
        userService.changeLockUser(user_id,true);
        return new ApiResponse<>(true);
    }

    @Operation(summary = "Get paginated users", description = "Retrieves a paginated list of users with optional filtering by keyword and staff role.")
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('user_read'))")
    public ApiResponse<PageResponse<UserBaseResponse>> getUsers(
            @Parameter(description = "Keyword to search by name or email")
            @RequestParam(value = "keyword", required = false) String keyword,

            @Parameter(description = "Filter by staff status (true for staff, false for customers)")
            @RequestParam(value = "isStaff", required = false) Boolean isStaff,

            @Parameter(description = "Field to sort by")
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sorting direction: 'asc' or 'desc'")
            @RequestParam(value = "direction", defaultValue = "desc") String direction,

            @Parameter(description = "Page index (1-based)")
            @RequestParam(value = "page", defaultValue = "1") int page,

            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        return new ApiResponse<>(userService.getUsers(keyword, isStaff, sortBy, direction, page, size));

    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user details by ID", description = "Retrieves detailed profile information for a specific user.")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('user_read'))")
    public ApiResponse<UserBaseResponse> getById(@PathVariable String userId) {
        return new ApiResponse<>(userService.getById(userId));
    }

}