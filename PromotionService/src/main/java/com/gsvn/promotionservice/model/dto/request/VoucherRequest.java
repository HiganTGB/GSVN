package com.gsvn.promotionservice.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class VoucherRequest {
    @NotBlank(message = "VOUCHER_CODE_REQUIRED")
    @Size(min = 3, max = 50, message = "VOUCHER_CODE_INVALID_SIZE")
    private String voucherCode;

    @NotBlank(message = "VOUCHER_NAME_REQUIRED")
    private String name;

    @NotBlank(message = "DISCOUNT_TYPE_REQUIRED")

    private String discountType;

    @NotNull(message = "DISCOUNT_VALUE_REQUIRED")
    @Min(value = 0, message = "DISCOUNT_VALUE_INVALID")
    private BigDecimal discountValue;

    @Min(value = 0, message = "MAX_DISCOUNT_INVALID")
    private BigDecimal maxDiscountAmount;

    @Min(value = 0, message = "MIN_ORDER_VALUE_INVALID")
    private BigDecimal minOrderValue;

    @NotNull(message = "USAGE_LIMIT_REQUIRED")
    @Min(value = 1, message = "USAGE_LIMIT_MIN_1")
    private Integer usageLimit;
    @NotNull(message = "LIMIT_PER_REQUIRED")
    @Min(value = 1, message = "LIMIT_PER_MIN_1")
    private Integer limitPer;

    @NotNull(message = "START_DATE_REQUIRED")
    private OffsetDateTime startDate;

    @NotNull(message = "END_DATE_REQUIRED")
    private OffsetDateTime endDate;

    private Boolean isActive = true;
}