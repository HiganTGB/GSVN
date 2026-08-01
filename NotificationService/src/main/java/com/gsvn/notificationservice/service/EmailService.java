package com.gsvn.notificationservice.service;

public interface EmailService {
    void sendOtp(String email);
    void sendResetPasswordEmail(String email, String token);
}
