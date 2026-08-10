package com.gsvn.orderservice.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gsvn.orderservice.model.enums.DeliveryMethod;
import com.gsvn.orderservice.model.enums.OrderStatus;
import com.gsvn.orderservice.model.enums.PaymentMethod;
import com.gsvn.orderservice.model.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderCode;
    private Long customerId;
    private String warehouseCode;

    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private String provinceCode;
    private String wardCode;
    private String addressDetail;
    private String customerNote;

    private PaymentMethod paymentMethod;
    private DeliveryMethod deliveryMethod;
    private PaymentStatus paymentStatus;

    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private BigDecimal totalRequiredNow;
    private BigDecimal amountPaid;

    private String voucherCode;
    private OrderStatus orderStatus;
    private String currentSagaId;
    private String clientIp;
    private String checkOutUrl;
    private String referenceId;

    private Long staffId;
    private Long confirmedBy;
    private OffsetDateTime confirmedAt;
    private String staffNote;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;


    private List<OrderItemResponse> items;
    @Data
    @Builder
    public static class OrderItemResponse {
        private String skuCode;
        private String productName;
        private String imageUrl;
        private LocalDate scheduledDate;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subPrice;
        private Boolean isPreorder;
        private Boolean isDepositApplied;
        private BigDecimal appliedDepositAmount;

    }
}