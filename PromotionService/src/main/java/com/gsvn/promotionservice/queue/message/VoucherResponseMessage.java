package com.gsvn.promotionservice.queue.message;

import lombok.*;
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