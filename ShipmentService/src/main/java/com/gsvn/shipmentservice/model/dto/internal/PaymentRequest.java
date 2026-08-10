package com.gsvn.shipmentservice.model.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    private String shipmentCode;
    private String orderCode;

    private String referenceId;
    private Long confirmedBy;

    private String externalTransactionId;

    private String provider;
    private String paymentMethod;

    private BigDecimal amount;
  //  private String currency;

    private String paymentType;// Balance

    private String providerResponse;
    private String note;
}
