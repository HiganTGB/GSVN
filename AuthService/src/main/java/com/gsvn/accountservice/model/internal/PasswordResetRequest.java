package com.gsvn.accountservice.model.internal;


import lombok.Data;

@Data
public class PasswordResetRequest {
    private String email;
    private String token;
}