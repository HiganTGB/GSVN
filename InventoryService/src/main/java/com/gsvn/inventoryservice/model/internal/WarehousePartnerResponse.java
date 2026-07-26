package com.gsvn.inventoryservice.model.internal;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehousePartnerResponse {
    private Integer id;
    private Integer warehouseCode;
    private String partnerName;
    private Integer shopId;
    private String partnerToken;
    private OffsetDateTime expiresAt;
    private OffsetDateTime updatedAt;
}