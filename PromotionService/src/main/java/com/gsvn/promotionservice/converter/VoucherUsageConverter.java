package com.gsvn.promotionservice.converter;

import com.gsvn.promotionservice.model.dto.response.VoucherUsageResponse;
import com.gsvn.promotionservice.model.entity.VoucherUsageHistory;
import org.springframework.stereotype.Component;

@Component
public class VoucherUsageConverter {

    public VoucherUsageResponse toResponse(VoucherUsageHistory entity) {
        if (entity == null) return null;
        return VoucherUsageResponse.builder()
                .id(entity.getId())
                .voucherId(entity.getVoucherId())
                .customerId(entity.getCustomerId())
                .guestEmail(entity.getGuestEmail())
                .orderId(entity.getOrderId())
                .usedAt(entity.getUsedAt())
                .build();
    }
}