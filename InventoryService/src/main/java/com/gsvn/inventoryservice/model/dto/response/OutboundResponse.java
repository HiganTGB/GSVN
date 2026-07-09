package com.gsvn.inventoryservice.model.dto.response;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboundResponse {
    private Long id;
    private Integer warehouseId;
    private String receiptCode;
    private String type;
    private String externalId;
    private Long staffId;
    private OffsetDateTime createdAt;
    private List<OutboundItemDetail> items;
    @Data
    @Builder
    public static class OutboundItemDetail {
        private Long skuId;
        private Integer quantity;
        private String skuCode;
        private String productName;
    }
}