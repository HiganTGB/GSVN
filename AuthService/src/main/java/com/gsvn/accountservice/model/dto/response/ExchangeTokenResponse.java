package com.gsvn.accountservice.model.dto.response;

public record ExchangeTokenResponse(String accessToken,
        Long expiresIn,
        String refreshToken,
        String scope,
        String tokenType) {
}
