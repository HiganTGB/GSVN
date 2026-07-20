package com.gsvn.paymentservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payment.vnpay")
@Getter
@Setter
public class VNPayProperties {
    private String payUrl;
    private String apiUrl;
    private String returnUrl;
    private String tmnCode;
    private String hashSecret;
    private String version;
    private String command;
    private String currCode;
    private String orderType;
}