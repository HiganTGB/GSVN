package com.gsvn.inventoryservice.model.entity;

import lombok.*;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboundReceipt {
    private Long id;
    private Integer warehouseId;
    private String receiptCode;
    private String type;
    private String externalId;
    private Long staffId;
    private String note;
    private OffsetDateTime createdAt;
}