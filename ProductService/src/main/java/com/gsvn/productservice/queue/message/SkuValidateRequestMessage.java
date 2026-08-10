package com.gsvn.productservice.queue.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuValidateRequestMessage {
    private String orderCode;      // Mã đơn hàng để track
    private String sagaId;           // ID của Saga để Orchestrator nhận diện khi tin quay về
    private List<SkuRequestItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuRequestItem {
        private String skuCode;
        private Integer quantity;
        private Boolean isPreorder;
    }
}