package com.gsvn.notificationservice.service;

import com.gsvn.notificationservice.model.entity.OtpDetails;


public interface OtpStorageService {
    void storeOtp(String email, String otp,int ttl);
    OtpDetails getOtpDetails(String email);
    void removeOtp(String email);
}
