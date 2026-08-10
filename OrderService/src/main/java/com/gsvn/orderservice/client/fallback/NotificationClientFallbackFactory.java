package com.gsvn.orderservice.client.fallback;

import com.gsvn.orderservice.client.NotificationClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationClientFallbackFactory implements FallbackFactory<NotificationClient> {

    @Override
    public NotificationClient create(Throwable cause) {
        return new NotificationClient() {
            @Override
            public void sendOrderUpdate(String orderCode, String message) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Notification Service call failed for orderCode: {}. Cause: {}",
                        orderCode, cause.getMessage());
            }
        };
    }
}