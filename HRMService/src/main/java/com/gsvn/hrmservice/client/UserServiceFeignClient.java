package com.gsvn.hrmservice.client;


import com.gsvn.hrmservice.common.ApiResponse;

import com.gsvn.hrmservice.config.InternalFeignConfig;
import com.gsvn.hrmservice.model.internal.*;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@FeignClient(
        name = "auth-service",
        contextId = "user",
        path = "/api/v1/users",
        configuration = InternalFeignConfig.class
)
public interface UserServiceFeignClient {
    @GetMapping("/{user_id}/roles")
    ApiResponse<Set<RoleResponse>> getUserRole(@PathVariable String user_id);
    @PutMapping("/{user_id}/roles")
    ApiResponse<Set<RoleResponse>> updateUserRole(@PathVariable String user_id, Set<Integer> roleIds);
    @PutMapping("/internal/{user_id}/lock")
    ApiResponse<Boolean> lockUser(@PathVariable("user_id") String userId);

    @PutMapping("/internal/{user_id}/unlock")
    ApiResponse<Boolean> unlockUser(@PathVariable("user_id") String userId);
    @PostMapping("/internal/create")
    ApiResponse<UserBaseResponse> create(@RequestBody UserBaseRequest request);
    @PostMapping("/internal/{user_id}/sync")
    ApiResponse<UserBaseResponse> sync(@PathVariable String user_id,@RequestBody @Valid SyncUserRequest request);

}