package com.gsvn.notificationservice.service;

import com.gsvn.notificationservice.model.entity.OtpDetails;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class OtpStorageService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_KEY_PREFIX = "OTP_";

    public OtpStorageService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeOtp(String email, String otp) {
        OtpDetails otpDetails = new OtpDetails(email, otp, LocalDateTime.now().plusMinutes(5));

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