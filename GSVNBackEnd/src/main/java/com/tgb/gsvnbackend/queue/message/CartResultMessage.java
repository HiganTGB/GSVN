package com.tgb.gsvnbackend.queue.message;

public record CartResultMessage(String cartId,
                                String orderId,
                                boolean success,
                                String urlPayment) {
}