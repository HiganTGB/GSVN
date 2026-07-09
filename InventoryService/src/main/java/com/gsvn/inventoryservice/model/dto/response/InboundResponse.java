package com.gsvn.inventoryservice.model.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboundResponse {
    private Long id;
    private Integer warehouseId;
    private Integer supplierId;
    private String receiptCode;
    private String sourceOutboundCode;
    private String type;
    private Long staffId;
    private String note;
    private OffsetDateTime createdAt;
    private List<InboundItemDetail> items;

    @Data
    @Builder
    public static class InboundItemDetail {
        private Long skuId;
        private Integer quantity;
        private String skuCode;
        private String productName;
        private BigDecimal importPrice;
        private BigDecimal lineTotal;
    }
}