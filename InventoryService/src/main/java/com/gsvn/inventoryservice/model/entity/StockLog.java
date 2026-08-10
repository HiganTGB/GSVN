package com.gsvn.inventoryservice.model.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLog {
    private Integer id;
    private Long skuId;
    private String skuCode;
    private Integer warehouseId;
    private Integer changePhysical;
    private Integer changeReserved;
    private String type;
    private String referenceId;
    private String note;
    private Long staffId;
    private String sagaId;
    private LocalDateTime createdAt;
}