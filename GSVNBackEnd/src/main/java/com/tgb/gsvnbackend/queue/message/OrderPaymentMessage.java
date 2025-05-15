package com.tgb.gsvnbackend.queue.message;

import com.tgb.gsvnbackend.model.enumeration.PaymentMethod;

public record OrderPaymentMessage( String orderId,String paymentId,
        String urlPayment,
        Boolean status
) {
}
