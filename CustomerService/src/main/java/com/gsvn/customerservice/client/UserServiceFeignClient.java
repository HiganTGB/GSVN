package com.gsvn.customerservice.client;



import com.gsvn.customerservice.client.fallback.UserServiceFeignClientFallbackFactory;
import com.gsvn.customerservice.common.ApiResponse;
import com.gsvn.customerservice.config.InternalFeignConfig;
import com.gsvn.customerservice.model.internal.SyncUserRequest;
import com.gsvn.customerservice.model.internal.UserBaseRequest;
import com.gsvn.customerservice.model.internal.UserBaseResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@FeignClient(
        name = "auth-service",
        contextId = "user",
        path = "/api/v1/users",
        configuration = InternalFeignConfig.class,
        fallbackFactory = UserServiceFeignClientFallbackFactory.class
)
public interface UserServiceFeignClient {
    @PostMapping("/internal/create")
    ApiResponse<UserBaseResponse> create(@RequestBody UserBaseRequest request);
    @PostMapping("/internal/{user_id}/sync")
    ApiResponse<UserBaseResponse> sync(@PathVariable String user_id,@RequestBody @Valid SyncUserRequest request);

}