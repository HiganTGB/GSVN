package com.gsvn.shipmentservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehousePartner {
    private Integer id;
    private String warehouseCode;
    private String partnerName;
    private Integer shopId;
    private String partnerToken;
    private OffsetDateTime expiresAt;
    private OffsetDateTime updatedAt;
}
