package com.tgb.gsvnbackend.queue.message;

public record OrderPaidMessage( String orderId,
        String paymentId,Boolean success
        ) {
}
