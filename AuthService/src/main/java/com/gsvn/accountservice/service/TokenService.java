package com.gsvn.accountservice.service;

import java.util.concurrent.TimeUnit;

public interface TokenService {

    void blacklistToken(String userId, String jti, long expirationMillis);

    boolean isTokenBlacklisted(String userId, String jti);

    void saveToken(String userId, String jti, long expiration, TimeUnit unit);

    void logoutAllDevices(String userId);

}