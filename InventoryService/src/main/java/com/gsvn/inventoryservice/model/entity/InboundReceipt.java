package com.gsvn.inventoryservice.model.entity;

import lombok.*;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboundReceipt {
    private Long id;
    private Integer warehouseId;
    private String receiptCode;
    private String sourceOutboundCode;
    private Integer supplierId;
    private String type;
    private Long staffId;
    private String note;
    private OffsetDateTime createdAt;
}