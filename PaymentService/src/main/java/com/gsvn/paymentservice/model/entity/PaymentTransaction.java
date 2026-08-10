package com.gsvn.paymentservice.model.entity;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {
    private Long id;
    private String shipmentCode;
    private String orderCode;

    private String referenceId;
    private Long confirmedBy;
    private OffsetDateTime confirmedAt;

    private String externalTransactionId;

    private String provider;
    private String paymentMethod;

    private BigDecimal amount;
    private String currency;

    // DEPOSIT, BALANCE, FULL, REFUND
    private String paymentType;

    // PENDING, SUCCESS, FAILED, CANCELLED
    private String status;

    private String checkoutUrl;
    private OffsetDateTime expiresAt;


    private String providerResponse;
    private String note;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}