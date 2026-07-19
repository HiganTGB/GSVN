package com.gsvn.orderservice.model.entity;

import com.gsvn.orderservice.model.enums.DeliveryMethod;
import com.gsvn.orderservice.model.enums.OrderStatus;
import com.gsvn.orderservice.model.enums.PaymentMethod;
import com.gsvn.orderservice.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
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
}