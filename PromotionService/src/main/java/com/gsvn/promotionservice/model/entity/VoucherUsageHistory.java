package com.gsvn.promotionservice.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherUsageHistory {
    private Long id;
    private Integer voucherId;
    private Long customerId;
    private String guestEmail;
    private Long orderId;
    private String sagaId;
    private OffsetDateTime usedAt;
}