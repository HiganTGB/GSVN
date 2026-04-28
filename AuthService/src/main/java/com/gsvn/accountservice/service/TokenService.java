package com.gsvn.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final StringRedisTemplate redisTemplate;

    private static final String BLACKLIST_KEY = "auth:blacklist:%s:%s"; // auth:blacklist:userId:jti
    private static final String WHITELIST_KEY = "auth:whitelist:%s:%s"; // auth:whitelist:userId:jti
    private static final String USER_SESSIONS_KEY = "auth:user_sessions:%s"; // auth:user_sessions:userId (Redis Set)

    public void blacklistToken(String userId, String jti, long expirationMillis) {
        if (expirationMillis <= 0) return;

        String key = String.format(BLACKLIST_KEY, userId, jti);
        redisTemplate.opsForValue().set(key, "revoked", expirationMillis, TimeUnit.MILLISECONDS);
        removeTokenFromWhitelist(userId, jti);
    }

    public boolean isTokenBlacklisted(String userId, String jti) {
        Boolean exists = redisTemplate.hasKey(String.format(BLACKLIST_KEY, userId, jti));
        return Boolean.TRUE.equals(exists);
    }

    public void saveToken(String userId, String jti, long expiration, TimeUnit unit) {
        String whitelistKey = String.format(WHITELIST_KEY, userId, jti);
        String sessionKey = String.format(USER_SESSIONS_KEY, userId);

        redisTemplate.opsForValue().set(whitelistKey, "active", expiration, unit);

        redisTemplate.opsForSet().add(sessionKey, jti);
        redisTemplate.expire(sessionKey, expiration, unit);
    }

    public void logoutAllDevices(String userId) {
        String sessionKey = String.format(USER_SESSIONS_KEY, userId);
        Set<String> jtis = redisTemplate.opsForSet().members(sessionKey);

        if (jtis != null && !jtis.isEmpty()) {
            Set<String> keysToDelete = jtis.stream()
                    .map(jti -> String.format(WHITELIST_KEY, userId, jti))
                    .collect(java.util.stream.Collectors.toSet());

            redisTemplate.delete(keysToDelete);
            log.info("Logged out all devices for user: {} ({} tokens)", userId, jtis.size());
        }
        redisTemplate.delete(sessionKey);
    }

    private void removeTokenFromWhitelist(String userId, String jti) {
        String whitelistKey = String.format(WHITELIST_KEY, userId, jti);
        String sessionKey = String.format(USER_SESSIONS_KEY, userId);

        redisTemplate.delete(whitelistKey);
        redisTemplate.opsForSet().remove(sessionKey, jti);
    }
}