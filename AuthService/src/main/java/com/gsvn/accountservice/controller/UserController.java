package com.gsvn.accountservice.controller;

import com.gsvn.accountservice.common.ApiResponse;

import com.gsvn.accountservice.model.dto.request.SyncUserRequest;
import com.gsvn.accountservice.model.dto.request.UserBaseRequest;

import com.gsvn.accountservice.model.dto.response.RoleResponse;
import com.gsvn.accountservice.model.dto.response.UserBaseResponse;

import com.gsvn.accountservice.service.RoleService;
import com.gsvn.accountservice.service.UserService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;
    RoleService roleService;
    @GetMapping("/my-info")
    ApiResponse<UserBaseResponse> getMyInfo() {
        return new ApiResponse<>(userService.getMyInfo());
    }
    @GetMapping("/my-roles")
    ApiResponse<Set<String>> getMyRoles() {
        return new ApiResponse<>(roleService.getMyRoles());
    }
    @GetMapping("/my-permissions")
    ApiResponse<Set<String>> getMyPermissions() {
        return new ApiResponse<>(roleService.getMyPermissions());
    }
    @GetMapping("/{user_id}/roles")
    ApiResponse<Set<RoleResponse>> getUserRole(@PathVariable String user_id){
        return new ApiResponse<>(roleService.getRolesByUserId(user_id));
    }
    @PutMapping("/{user_id}/roles")
    ApiResponse<Set<RoleResponse>> updateUserRole(@PathVariable String user_id, Set<Integer> roleIds){
        return new ApiResponse<>(roleService.updateRolesByUserId(user_id,roleIds));
    }
    @PutMapping("/{user_id}/lock")
    ApiResponse<Boolean> lockUser(@PathVariable String user_id){
        userService.changeLockUser(user_id,false);
        return new ApiResponse<>(true);
    }
    @PutMapping("/{user_id}/unlock")
    ApiResponse<Boolean> unlockUser(@PathVariable String user_id){
        userService.changeLockUser(user_id,true);
        return new ApiResponse<>(true);
    }
    @PostMapping("/internal/create")
    ApiResponse<UserBaseResponse> create(@RequestBody @Valid UserBaseRequest request) {
        return new ApiResponse<>(userService.create(request,true));
    }
    @PostMapping("/internal/{user_id}/sync")
    ApiResponse<UserBaseResponse> sync(@PathVariable String user_id,@RequestBody @Valid SyncUserRequest request) {
        return new ApiResponse<>(userService.syncUser(user_id,request));
    }


}