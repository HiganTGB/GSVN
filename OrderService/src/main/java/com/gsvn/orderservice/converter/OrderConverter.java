package com.gsvn.orderservice.converter;

import com.gsvn.orderservice.model.dto.request.OrderCreateRequest;
import com.gsvn.orderservice.model.dto.request.OrderItemRequest;
import com.gsvn.orderservice.model.dto.response.OrderResponse;
import com.gsvn.orderservice.model.entity.Order;
import com.gsvn.orderservice.model.entity.OrderItem;
import com.gsvn.orderservice.model.enums.DeliveryMethod;
import com.gsvn.orderservice.model.enums.OrderStatus;
import com.gsvn.orderservice.model.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderConverter {

    public Order toEntity(OrderCreateRequest request) {
        if (request == null) return null;

        return Order.builder()
                .customerId(request.getCustomerId())
                .warehouseCode(request.getWarehouseCode())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .receiverEmail(request.getReceiverEmail())
                .provinceCode(request.getProvinceCode())
                .wardCode(request.getWardCode())
                .addressDetail(request.getAddressDetail())
                .customerNote(request.getCustomerNote())
                .paymentMethod(request.getPaymentMethod())
                .deliveryMethod(request.getDeliveryMethod())
                .voucherCode(request.getVoucherCode())

                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)

                .totalAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .finalAmount(BigDecimal.ZERO)
                .totalRequiredNow(BigDecimal.ZERO)
                .amountPaid(BigDecimal.ZERO)
                .build();
    }

    public List<OrderItem> toItemEntities(List<OrderItemRequest> requests, Long orderId) {
        if (requests == null || requests.isEmpty()) return Collections.emptyList();

        return requests.stream()
                .map(request -> toItemEntity(request, orderId))
                .collect(Collectors.toList());
    }

    private OrderItem toItemEntity(OrderItemRequest request, Long orderId) {
        if (request == null) return null;

        return OrderItem.builder()
                .orderId(orderId)
                .quantity(request.getQuantity())

                .isPreorder(Boolean.TRUE.equals(request.getIsPreorder()))
                .isDepositApplied(Boolean.TRUE.equals(request.getIsDepositApplied()))

                .unitPrice(BigDecimal.ZERO)
                .subPrice(BigDecimal.ZERO)
                .appliedDepositAmount(BigDecimal.ZERO)

                .productName("")
                .skuCode(request.getSkuCode())
                .build();
    }

    public void updateEntity(OrderCreateRequest request, Order entity) {
        if (request == null || entity == null) return;

        entity.setCustomerId(request.getCustomerId());
        entity.setWarehouseCode(request.getWarehouseCode());
        entity.setReceiverName(request.getReceiverName());
        entity.setReceiverPhone(request.getReceiverPhone());
        entity.setReceiverEmail(request.getReceiverEmail());
        entity.setProvinceCode(request.getProvinceCode());
        entity.setWardCode(request.getWardCode());
        entity.setAddressDetail(request.getAddressDetail());
        entity.setCustomerNote(request.getCustomerNote());
        entity.setDeliveryMethod(request.getDeliveryMethod());
    }
    public OrderResponse toResponse(Order entity) {
        if (entity == null) {
            return null;
        }
        return toResponse(entity, Collections.emptyList());
    }

    public OrderResponse toResponse(Order entity, List<OrderItem> itemEntities) {
        if (entity == null) {
            return null;
        }

        return OrderResponse.builder()

                .id(entity.getId())
                .orderCode(entity.getOrderCode())
                .customerId(entity.getCustomerId())
                .warehouseCode(entity.getWarehouseCode())


                .receiverName(entity.getReceiverName())
                .receiverPhone(entity.getReceiverPhone())
                .receiverEmail(entity.getReceiverEmail())
                .provinceCode(entity.getProvinceCode())
                .wardCode(entity.getWardCode())
                .addressDetail(entity.getAddressDetail())
                .customerNote(entity.getCustomerNote())


                .paymentMethod(entity.getPaymentMethod())
                .deliveryMethod(entity.getDeliveryMethod())
                .paymentStatus(entity.getPaymentStatus())
                .orderStatus(entity.getOrderStatus())


                .totalAmount(entity.getTotalAmount())
                .discountAmount(entity.getDiscountAmount())
                .finalAmount(entity.getFinalAmount())
                .totalRequiredNow(entity.getTotalRequiredNow())
                .amountPaid(entity.getAmountPaid())


                .voucherCode(entity.getVoucherCode())
                .currentSagaId(entity.getCurrentSagaId())
                .clientIp(entity.getClientIp())
                .checkOutUrl(entity.getCheckOutUrl())
                .referenceId(entity.getReferenceId())


                .staffId(entity.getStaffId())
                .confirmedBy(entity.getConfirmedBy())
                .confirmedAt(entity.getConfirmedAt())
                .staffNote(entity.getStaffNote())


                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())


                .items(toItemResponseList(itemEntities))
                .build();
    }

    public List<OrderResponse> toResponseList(List<Order> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private List<OrderResponse.OrderItemResponse> toItemResponseList(List<OrderItem> items) {
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .skuCode(item.getSkuCode())
                        .productName(item.getProductName())
                        .imageUrl(item.getImageUrl())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subPrice(item.getSubPrice())
                        .scheduledDate(item.getScheduledDate())
                        .isDepositApplied(item.getIsDepositApplied())
                        .appliedDepositAmount(item.getAppliedDepositAmount())
                        .isPreorder(item.getIsPreorder())
                        .build())
                .collect(Collectors.toList());
    }
}