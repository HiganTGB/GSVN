package com.gsvn.paymentservice.queue.message;

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
public class PaymentResponseMessage {
    private String sagaId;
    private String orderCode;
    private String referenceId;


    private String status;
    private String checkoutUrl;

    private String paymentMethod;
    private String message;
    private OffsetDateTime expiresAt;
}