package com.gsvn.paymentservice.queue.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedMessage {
    private String sagaId;
    private String orderCode;
    private String referenceId;

    private String status;
    private BigDecimal amountPaid;
    private String externalTransactionId;

    private String paymentMethod;
    private String paymentType;
    private OffsetDateTime completionTime;

    private String gatewayResponseCode;
}