package com.gsvn.orderservice.client;

import com.gsvn.orderservice.client.fallback.NotificationClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service",contextId = "notifications",path = "/api/v1/notifications",fallbackFactory = NotificationClientFallbackFactory.class)
public interface NotificationClient {
    @PostMapping("/internal/{orderCode}")
    void sendOrderUpdate(@PathVariable String orderCode, @RequestBody String message);
}