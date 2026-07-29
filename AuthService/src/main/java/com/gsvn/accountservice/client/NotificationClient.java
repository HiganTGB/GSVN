package com.gsvn.accountservice.client;

import com.gsvn.accountservice.common.ApiResponse;
import com.gsvn.accountservice.model.internal.PasswordResetRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service",configuration = InternalFeignConfig.class)
public interface NotificationClient {
    @PostMapping("/api/v1/notifications/email/reset-password")
    ApiResponse<String> sendResetPasswordEmail(@RequestBody PasswordResetRequest request);
}