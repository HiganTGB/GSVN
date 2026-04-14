package com.gsvn.accountservice.model.dto.response;

public record ForgotPasswordEmailRequest(String email,String token) {
}
