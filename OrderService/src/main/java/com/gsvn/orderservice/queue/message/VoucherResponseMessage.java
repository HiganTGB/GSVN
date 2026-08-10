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
public class VoucherResponseMessage {
    private String orderCode;
    private String sagaId;
    private boolean success;
    private BigDecimal discountAmount;
    private String errorMessage;
}