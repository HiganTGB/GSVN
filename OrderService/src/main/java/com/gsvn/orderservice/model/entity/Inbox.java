package com.gsvn.orderservice.model.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class Inbox {
    private String eventId;
    private String eventType;
    private String payload;
    private String status;
    private String errorLog;
    private OffsetDateTime processedAt;
    private OffsetDateTime receivedAt;
}