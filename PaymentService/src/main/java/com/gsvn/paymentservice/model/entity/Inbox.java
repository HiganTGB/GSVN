package com.gsvn.paymentservice.model.entity;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inbox {
    private String eventId;
    private String eventType;
    private String payload; // JSON String
    private String status;  // PENDING, PROCESSED, FAIL
    private String errorLog;
    private OffsetDateTime processedAt;
    private OffsetDateTime receivedAt;
}