package com.gsvn.notificationservice.service.impl;

import com.gsvn.notificationservice.model.entity.OtpDetails;
import com.gsvn.notificationservice.service.OtpStorageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class OtpStorageServiceImpl implements OtpStorageService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_KEY_PREFIX = "OTP_";

    public OtpStorageServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeOtp(String email, String otp,int ttl) {
        OtpDetails otpDetails = new OtpDetails(email, otp, LocalDateTime.now().plusMinutes(ttl));

        redisTemplate.opsForValue().set(
                REDIS_KEY_PREFIX + email,
                otpDetails,
                5,
                TimeUnit.MINUTES
        );
    }

    public OtpDetails getOtpDetails(String email) {
        return (OtpDetails) redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + email);
    }

    public void removeOtp(String email) {
        redisTemplate.delete(REDIS_KEY_PREFIX + email);
    }
}