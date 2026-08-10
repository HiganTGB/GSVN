package com.gsvn.orderservice.queue.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestMessage {

    private String sagaId;
    private String orderCode;


    private BigDecimal amount;
    private String orderInfo;
    private String orderType;


    private String bankCode;
    private String locale;


    private String clientIpAddress;

    private String paymentMethod;
    private String paymentType;


    private Integer expireMinutes;
}