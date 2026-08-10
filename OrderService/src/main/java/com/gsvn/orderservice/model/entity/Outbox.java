package com.gsvn.orderservice.model.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class Outbox {
    private String id;
    private String aggregateId;
    private String eventType;
    private String payload;
    private String status;
    private Integer retryCount;
    private OffsetDateTime lastAttemptAt;
    private OffsetDateTime createdAt;
}