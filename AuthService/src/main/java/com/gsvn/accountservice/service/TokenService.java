package com.gsvn.accountservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Service
@RequiredArgsConstructor
public class TokenService {
    private final StringRedisTemplate redisTemplate;

    private static final String BLACKLIST_PREFIX = "bl:";
    private static final String WHITELIST_PREFIX = "wl:";

    private String buildKey(String prefix, String userId, String jti) {
        return prefix + userId + ":" + jti;
    }

    public void blacklistToken(String userId, String jti, long expirationMillis) {
        if (expirationMillis <= 0) return;
        String key = buildKey(BLACKLIST_PREFIX, userId, jti);
        redisTemplate.opsForValue().set(key, "1", expirationMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String userId, String jti) {
        return redisTemplate.hasKey(buildKey(BLACKLIST_PREFIX, userId, jti));
    }

    public void saveToken(String userId, String jti, long expiration, TimeUnit unit) {
        String key = buildKey(WHITELIST_PREFIX, userId, jti);
        redisTemplate.opsForValue().set(key, "1", expiration, unit);
    }

    public void logoutAllDevices(String userId) {
        String pattern = WHITELIST_PREFIX + userId + ":*";
        var keys = redisTemplate.keys(pattern);
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        //TODO : add to blacklist
    }
}