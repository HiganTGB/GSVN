package com.tgb.gsvnbackend.model.domain;

import java.math.BigDecimal;

public record PaymentDomain(String orderId,String userId,
                            BigDecimal amount,
                            String paymentMethod, String ipAddress) {

}
