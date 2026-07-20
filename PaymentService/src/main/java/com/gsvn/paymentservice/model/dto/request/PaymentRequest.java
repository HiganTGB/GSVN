package com.gsvn.paymentservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String shipmentCode;
    private String orderCode;

    private String referenceId; // shipmentCode
    private Long confirmedBy; // staff who confirm

    private String externalTransactionId;

    private String provider;
    private String paymentMethod;

    private BigDecimal amount;
 //   private String currency;

    private String paymentType;// Balance

    private String providerResponse;
    private String note;
}
