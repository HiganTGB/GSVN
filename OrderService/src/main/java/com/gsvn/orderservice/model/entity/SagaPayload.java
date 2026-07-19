package com.gsvn.orderservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaPayload {
    private String transactionId;

    private String inventoryReservationId;

    private Integer appliedVoucherId;
    private String voucherCode;

    private String paymentUrl;
    private String paymentTransactionId;
}