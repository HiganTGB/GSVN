package com.gsvn.shipmentservice.model.entity;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class Inbox {
    private UUID eventId;
    private String eventType;
    private String payload;
    private String status;
    private String errorLog;
    private OffsetDateTime processedAt;
    private OffsetDateTime receivedAt;
}