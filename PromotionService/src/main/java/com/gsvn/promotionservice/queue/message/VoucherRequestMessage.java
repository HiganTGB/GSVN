package com.gsvn.promotionservice.queue.message;


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
public class VoucherRequestMessage {
    private String orderCode;
    private Long orderId;
    private String sagaId;
    private String voucherCode;
    private Long customerId;
    private String guestEmail;
    private BigDecimal totalAmount;
}