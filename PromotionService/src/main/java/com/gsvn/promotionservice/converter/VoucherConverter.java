package com.gsvn.promotionservice.converter;

import com.gsvn.promotionservice.model.dto.request.VoucherRequest;
import com.gsvn.promotionservice.model.dto.response.VoucherResponse;
import com.gsvn.promotionservice.model.entity.Voucher;
import org.springframework.stereotype.Component;

@Component
public class VoucherConverter {

    public Voucher toEntity(VoucherRequest request) {
        if (request == null) return null;
        return Voucher.builder()
                .voucherCode(request.getVoucherCode())
                .name(request.getName())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minOrderValue(request.getMinOrderValue())
                .limitPer(request.getLimitPer())
                .usageLimit(request.getUsageLimit())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }

    public VoucherResponse toResponse(Voucher entity) {
        if (entity == null) return null;
        return VoucherResponse.builder()
                .id(entity.getId())
                .voucherCode(entity.getVoucherCode())
                .name(entity.getName())
                .discountType(entity.getDiscountType())
                .maxDiscountAmount(entity.getMaxDiscountAmount())
                .discountValue(entity.getDiscountValue())
                .minOrderValue(entity.getMinOrderValue())
                .usageLimit(entity.getUsageLimit())
                .usedCount(entity.getUsedCount())
                .isActive(entity.getIsActive())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}