package com.gsvn.shipmentservice.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class WarehousePartnerResponse {
    private Integer id;
    private String warehouseCode;
    private String partnerName;
    private Integer shopId;
    private String partnerToken;
    private OffsetDateTime expiresAt;
    private OffsetDateTime updatedAt;
}