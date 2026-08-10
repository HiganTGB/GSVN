package com.gsvn.shipmentservice.model.entity;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class Outbox {
    private UUID id;
    private UUID aggregateId;
    private String eventType;
    private String payload;
    private String status;
    private Integer retryCount;
    private OffsetDateTime lastAttemptAt;
    private OffsetDateTime createdAt;
}