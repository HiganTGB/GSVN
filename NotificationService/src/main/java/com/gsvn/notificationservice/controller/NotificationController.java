package com.gsvn.notificationservice.controller;

import com.gsvn.notificationservice.common.ApiResponse;

import com.gsvn.notificationservice.model.dto.request.PasswordResetRequest;
import com.gsvn.notificationservice.service.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService mailService;

    @PostMapping("/email/reset-password")
    public ApiResponse<String> sendResetPasswordEmail(@RequestBody PasswordResetRequest request) {
        mailService.sendResetPasswordEmail(request.getEmail(), request.getToken());
        return new ApiResponse<>("Email khôi phục mật khẩu đã được gửi thành công.");
    }
}