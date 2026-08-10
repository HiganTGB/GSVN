package com.gsvn.notificationservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/notifications")
public class SSEController {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/subscribe/{orderCode}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String orderCode, HttpServletResponse response) {
        response.setHeader("X-Accel-Filtering", "no");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        SseEmitter emitter = new SseEmitter(300_000L);

        emitters.put(orderCode, emitter);

        emitter.onCompletion(() -> emitters.remove(orderCode));
        emitter.onTimeout(() -> emitters.remove(orderCode));
        emitter.onError((e) -> emitters.remove(orderCode));
        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected"));
        } catch (IOException e) {
            emitters.remove(orderCode);
        }
        return emitter;
    }

    @PostMapping("/internal/{orderCode}")
    public void sendNotification(@PathVariable String orderCode, @RequestBody String message) {
        SseEmitter emitter = emitters.get(orderCode);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("order-update")
                        .data(message));
            } catch (IOException e) {
                emitters.remove(orderCode);
            }
        }
    }
}