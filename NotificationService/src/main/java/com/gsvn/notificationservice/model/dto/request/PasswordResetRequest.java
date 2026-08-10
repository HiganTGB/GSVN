package com.gsvn.notificationservice.model.dto.request;


import lombok.Data;

@Data
public class PasswordResetRequest {
    private String email;
    private String token;
}