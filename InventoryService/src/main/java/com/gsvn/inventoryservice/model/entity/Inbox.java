package com.gsvn.inventoryservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inbox {
    private String eventId;
    private String eventType;
    private String payload;
    private String status;    // PENDING, PROCESSED, FAIL
    private String errorLog;
    private OffsetDateTime processedAt;
    private OffsetDateTime receivedAt;
}