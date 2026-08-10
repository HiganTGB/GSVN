package com.gsvn.promotionservice.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class VoucherUsageResponse {
    private Long id;
    private Integer voucherId;
    private Long customerId;
    private String guestEmail;
    private Long orderId;
    private OffsetDateTime usedAt;
}