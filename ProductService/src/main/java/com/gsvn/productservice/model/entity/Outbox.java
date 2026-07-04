package com.gsvn.productservice.model.entity;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Outbox {
    private String id;
    private String aggregateId;
    private String eventType;
    private String payload;
    private String status;     // PENDING, SENT, FAILED
    private Integer retryCount;
    private OffsetDateTime lastAttemptAt;
    private OffsetDateTime createdAt;
}