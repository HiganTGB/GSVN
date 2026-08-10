package com.gsvn.paymentservice.service;


import com.gsvn.paymentservice.model.dto.VNPayResponse;
import com.gsvn.paymentservice.model.entity.PaymentTransaction;
import com.gsvn.paymentservice.queue.message.PaymentRequestMessage;

import java.util.Map;

public interface VNPayService {
    PaymentTransaction createPaymentUrl(PaymentRequestMessage message);
    VNPayResponse processIPN(Map<String, String> params);
    boolean verifyCallback(Map<String, String> queryParams);
}