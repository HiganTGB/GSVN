package com.gsvn.productservice.model.entity;

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
    private String payload;
    private String status;    // PENDING, PROCESSED, FAIL
    private String errorLog;
    private OffsetDateTime processedAt;
    private OffsetDateTime receivedAt;
}