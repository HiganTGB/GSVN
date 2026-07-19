package com.gsvn.orderservice.model.entity;

import com.gsvn.orderservice.model.saga.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSagaInstance {
    private String sagaId;
    private Long orderId;
    private String currentStep;
    private SagaStatus status;
    private SagaPayload payload;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}