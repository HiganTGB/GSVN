package com.gsvn.shipmentservice.model.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehousePartnerRequest {

    @NotBlank(message = "PARTNER_NAME_REQUIRED")
    @Size(max = 100, message = "PARTNER_NAME_TOO_LONG")
    private String partnerName;

    @NotNull(message = "SHOP_ID_REQUIRED")
    private Integer shopId;

    @NotBlank(message = "PARTNER_TOKEN_REQUIRED")
    @Size(max = 1000, message = "PARTNER_TOKEN_TOO_LONG")
    private String partnerToken;;

    @Future(message = "EXPIRY_DATE_MUST_BE_IN_FUTURE")
    private OffsetDateTime expiresAt;
}