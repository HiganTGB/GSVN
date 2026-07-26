package com.gsvn.orderservice.queue.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuValidateResponseMessage {
    private String orderCode;
    private String sagaId;

    private boolean hasError;      // Boolean lỗi
    private String errorMessage;   // Message lỗi (nếu có)

    private List<SkuResponseItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuResponseItem {
        // Thông tin sản phẩm & SKU
        private Integer productId;
        private String productName;
        private Long skuId;
        private String skuCode;
        private String imageUrl;

        // Giá cả
        private BigDecimal sellingPrice;      // sub giá available
        private BigDecimal prePrice;          // sub giá pre-order
        private BigDecimal preDepositAmount;  // giá đặt cọc

        // Trạng thái
        private Boolean isPreorderItem;       // là món cho preorder (theo cấu hình product)
        private Integer quantity;             // số lượng (từ gửi sang)

        // Ngày phát hành (nếu là preorder)
        private LocalDate preReleaseDate;
    }
}