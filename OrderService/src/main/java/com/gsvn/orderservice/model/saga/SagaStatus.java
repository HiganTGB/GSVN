package com.gsvn.orderservice.model.saga;

public enum SagaStatus {
    STARTED,
    SUCCEEDED,
    COMPENSATING,
    COMPENSATED,
    FAILED
}