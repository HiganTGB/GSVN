package com.gsvn.accountservice.model.dto.response;

public record AuthenticationResponse(String accessToken,String refreshToken, boolean authenticated) {
}
