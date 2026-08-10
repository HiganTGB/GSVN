package com.gsvn.orderservice.queue.message;

import lombok.*;
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