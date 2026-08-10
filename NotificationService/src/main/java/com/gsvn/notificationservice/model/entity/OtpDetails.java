package com.gsvn.notificationservice.model.entity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OtpDetails implements Serializable {
    private String email;
    private String otp;
    private LocalDateTime expirationTime;
}