package com.tgb.gsvnbackend.queue.message;

import com.tgb.gsvnbackend.model.enumeration.PaymentMethod;

import java.math.BigDecimal;

public record PaymentInitMessage(String orderId,String userId,
       BigDecimal amount,
       String paymentMethod, String ipAddress) {

}
