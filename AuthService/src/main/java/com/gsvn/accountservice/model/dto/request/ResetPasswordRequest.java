package com.gsvn.accountservice.model.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}