package com.gsvn.orderservice.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsvn.orderservice.client.NotificationClient;
import com.gsvn.orderservice.exc.AppException;
import com.gsvn.orderservice.exc.ErrorCode;
import com.gsvn.orderservice.mapper.*;
import com.gsvn.orderservice.model.entity.*;
import com.gsvn.orderservice.model.enums.OrderStatus;
import com.gsvn.orderservice.model.enums.PaymentMethod;
import com.gsvn.orderservice.model.enums.PaymentStatus;
import com.gsvn.orderservice.model.saga.OrderEventType;
import com.gsvn.orderservice.model.saga.SagaStatus;
import com.gsvn.orderservice.model.saga.SagaStep;
import com.gsvn.orderservice.queue.message.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderInboxProcessor {

    private static final String STATUS_PROCESSED = "PROCESSED";
    private static final String STATUS_FAIL = "FAIL";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_SUCCESS = "SUCCESS";

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderSagaInstanceMapper sagaMapper;
    private final MessageLogMapper logMapper;
    private final NotificationClient notificationClient;
    private final ObjectMapper mapper;

    @Value("${app.order-time-out}")
    private Integer timeout;

    @Transactional
    public void execute(Inbox inbox) {
        if (STATUS_PROCESSED.equals(inbox.getStatus())) return;

        try {
            processEventByType(inbox);
            logMapper.updateInboxStatus(inbox.getEventId(), STATUS_PROCESSED, null);
        } catch (AppException e) {
            log.error("Business Error processing inbox {}: {}", inbox.getEventId(), e.getErrorCode().getMessage());
            logMapper.updateInboxStatus(inbox.getEventId(), STATUS_FAIL, e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("System Error processing inbox {}: {}", inbox.getEventId(), e.getMessage());
            logMapper.updateInboxStatus(inbox.getEventId(), STATUS_FAIL, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void processEventByType(Inbox inbox) {
        switch (inbox.getEventType()) {
            case "SKU_VALIDATE_RES": handleSkuValidation(inbox); break;
            case "INVENTORY_RESERVE_RES": handleInventoryReservation(inbox); break;
            case "VOUCHER_APPLY_RES": handleVoucherResponse(inbox); break;
            case "PAYMENT_URL_RES": handlePaymentResponse(inbox); break;
            case "PAYMENT_COMPLETED_EVENT": handlePaymentCompleted(inbox); break;
            case "PAYMENT_FAILED_EVENT": handlePaymentFailed(inbox); break;
            default: log.warn("Unknown event type: {}", inbox.getEventType());
        }
    }

    private void handlePaymentResponse(Inbox inbox) {
        PaymentResponseMessage res = parsePayload(inbox.getPayload(), PaymentResponseMessage.class);
        Order order = getOrderOrThrow(res.getOrderCode());

        if (!STATUS_SUCCESS.equalsIgnoreCase(res.getStatus())) {
            handleFailedSaga(order, "Payment Gateway Error: " + res.getMessage());
            return;
        }

        order.setCheckOutUrl(res.getCheckoutUrl());
        order.setReferenceId(res.getReferenceId());

        var method = order.getPaymentMethod();

        if (PaymentMethod.VNPAY.equals(method)) {
            order.setOrderStatus(OrderStatus.VALIDATED);
            updateSagaStatus(order.getCurrentSagaId(), SagaStep.PAYMENT_URL_GENERATED, SagaStatus.SUCCEEDED);
            notificationClient.sendOrderUpdate(order.getOrderCode(), res.getCheckoutUrl());
        } else {
            order.setOrderStatus(OrderStatus.AWAITING);
            updateSagaStatus(order.getCurrentSagaId(), SagaStep.COMPLETED, SagaStatus.SUCCEEDED);
            notificationClient.sendOrderUpdate(order.getOrderCode(), STATUS_SUCCESS);
        }
        orderMapper.updateAllFields(order);
    }

    private void handlePaymentCompleted(Inbox inbox) {
        PaymentCompletedMessage res = parsePayload(inbox.getPayload(), PaymentCompletedMessage.class);
        Order order = getOrderOrThrow(res.getOrderCode());

        if (OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
            log.warn("Payment received for CANCELLED order: {}", order.getOrderCode());
            return;
        }
        if (STATUS_SUCCESS.equalsIgnoreCase(res.getStatus())) {
            BigDecimal alreadyPaid = order.getAmountPaid() != null ? order.getAmountPaid() : BigDecimal.ZERO;
            BigDecimal newTotalPaid = alreadyPaid.add(res.getAmountPaid());
            order.setAmountPaid(newTotalPaid);

            boolean isFullyPaid = newTotalPaid.compareTo(order.getFinalAmount()) >= 0;
            order.setPaymentStatus(isFullyPaid ? PaymentStatus.FULLY_PAID : PaymentStatus.PARTIALLY_PAID);

            var method = order.getPaymentMethod();
            if (PaymentMethod.VNPAY.equals(method)) {
                order.setOrderStatus(OrderStatus.AWAITING);
                updateSagaStatus(order.getCurrentSagaId(), SagaStep.COMPLETED, SagaStatus.SUCCEEDED);
                notificationClient.sendOrderUpdate(order.getOrderCode(), "PAYMENT_SUCCESS");
            }
        }
        orderMapper.updateAllFields(order);
    }

    private void handlePaymentFailed(Inbox inbox) {
        String orderCode = extractOrderCode(inbox);
        Order order = getOrderOrThrow(orderCode);
        handleFailedSaga(order, "Payment timed out or failed");
    }

    private void handleSkuValidation(Inbox inbox) {
        SkuValidateResponseMessage response = parsePayload(inbox.getPayload(), SkuValidateResponseMessage.class);
        Order order = getOrderOrThrow(response.getOrderCode());

        if (response.isHasError()) {
            handleFailedSaga(order, "Validation failed: " + response.getErrorMessage());
            return;
        }

        List<OrderItem> currentItems = orderItemMapper.findByOrderId(order.getId());
        Map<String, SkuValidateResponseMessage.SkuResponseItem> resMap = response.getItems().stream()
                .collect(Collectors.toMap(SkuValidateResponseMessage.SkuResponseItem::getSkuCode, i -> i));

        calculateAndUpdateOrderFinancials(order, currentItems, resMap);
        updateSagaStatus(order.getCurrentSagaId(), SagaStep.INVENTORY_RESERVING, SagaStatus.STARTED);
        sendInventoryReserveRequest(order, currentItems);
    }

    private void handleInventoryReservation(Inbox inbox) {
        InventoryResponseMessage response = parsePayload(inbox.getPayload(), InventoryResponseMessage.class);
        Order order = getOrderOrThrow(response.getOrderCode());

        if (!response.isSuccess()) {
            handleFailedSaga(order, "Inventory reservation failed: " + response.getErrorMessage());
            return;
        }

        if (order.getVoucherCode() != null && !order.getVoucherCode().isBlank()) {
            updateSagaStatus(order.getCurrentSagaId(), SagaStep.VOUCHER_APPLYING, SagaStatus.STARTED);
            sendVoucherApplyRequest(order);
        } else {
            processNextStepAfterValidation(order);
        }
    }

    private void handleVoucherResponse(Inbox inbox) {
        VoucherResponseMessage res = parsePayload(inbox.getPayload(), VoucherResponseMessage.class);
        Order order = getOrderOrThrow(res.getOrderCode());

        if (res.isSuccess()) {
            applyVoucherDiscountToOrder(order, res.getDiscountAmount());
            processNextStepAfterValidation(order);
        } else {
            handleFailedSaga(order, "Voucher failed: " + res.getErrorMessage());
        }
    }

    // --- HELPER METHODS ---

    private <T> T parsePayload(String payload, Class<T> clazz) {
        try {
            return mapper.readValue(payload, clazz);
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY);
        }
    }

    private Order getOrderOrThrow(String orderCode) {
        return orderMapper.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
    }

    private String extractOrderCode(Inbox inbox) {
        try {
            if (inbox.getPayload().contains("{")) {
                return mapper.readTree(inbox.getPayload()).get("orderCode").asText();
            }
            return inbox.getPayload().replace("\"", "");
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private void calculateAndUpdateOrderFinancials(Order order, List<OrderItem> currentItems, Map<String, SkuValidateResponseMessage.SkuResponseItem> resMap) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalRequiredNow = BigDecimal.ZERO;

        for (OrderItem item : currentItems) {
            SkuValidateResponseMessage.SkuResponseItem resItem = resMap.get(item.getSkuCode());
            if (resItem == null) continue;

            // Map thông tin sản phẩm
            item.setSkuCode(resItem.getSkuCode());
            item.setProductName(resItem.getProductName());
            item.setImageUrl(resItem.getImageUrl());
            item.setScheduledDate(resItem.getPreReleaseDate());

            BigDecimal unitPrice = Boolean.TRUE.equals(item.getIsPreorder()) ? resItem.getPrePrice() : resItem.getSellingPrice();
            item.setUnitPrice(unitPrice);
            item.setSubPrice(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));

            // Tính toán cọc (Deposit)
            boolean canDeposit = Boolean.TRUE.equals(item.getIsPreorder()) &&
                    resItem.getPreDepositAmount() != null &&
                    resItem.getPreDepositAmount().compareTo(BigDecimal.ZERO) > 0;

            if (canDeposit && Boolean.TRUE.equals(item.getIsDepositApplied())) {
                item.setAppliedDepositAmount(resItem.getPreDepositAmount());
                totalRequiredNow = totalRequiredNow.add(item.getAppliedDepositAmount().multiply(BigDecimal.valueOf(item.getQuantity())));
            } else {
                item.setAppliedDepositAmount(BigDecimal.ZERO);
                totalRequiredNow = totalRequiredNow.add(item.getSubPrice());
            }

            totalAmount = totalAmount.add(item.getSubPrice());
            orderItemMapper.updateAllFields(item);
        }

        order.setTotalAmount(totalAmount);
        order.setFinalAmount(totalAmount.subtract(order.getDiscountAmount()));
        order.setTotalRequiredNow(totalRequiredNow);
        log.info(order.toString());
        orderMapper.updateAllFields(order);
    }

    private void applyVoucherDiscountToOrder(Order order, BigDecimal discountAmount) {
        order.setDiscountAmount(discountAmount);
        BigDecimal finalAmount = order.getTotalAmount().subtract(discountAmount);
        order.setFinalAmount(finalAmount);

        BigDecimal updatedRequiredNow = order.getTotalRequiredNow().subtract(discountAmount);
        order.setTotalRequiredNow(updatedRequiredNow.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : updatedRequiredNow);
    }

    private void processNextStepAfterValidation(Order order) {
        order.setOrderStatus(OrderStatus.VALIDATED);
        orderMapper.updateAllFields(order);
        updateSagaStatus(order.getCurrentSagaId(), SagaStep.PAYMENT_URL_GENERATING, SagaStatus.STARTED);
        createPaymentRequestOutbox(order);
    }

    private void insertOutbox(String sagaId, OrderEventType type, Object payload) {
        try {
            Outbox outbox = Outbox.builder()
                    .id(UUID.randomUUID().toString())
                    .aggregateId(sagaId)
                    .eventType(type.name())
                    .payload(mapper.writeValueAsString(payload))
                    .status(STATUS_PENDING)
                    .retryCount(0)
                    .build();
            logMapper.insertOutbox(outbox);
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private void updateSagaStatus(String sagaId, SagaStep step, SagaStatus status) {
        OrderSagaInstance saga = OrderSagaInstance.builder()
                .sagaId(sagaId)
                .currentStep(step.name())
                .status(status)
                .build();
        sagaMapper.updateStepAndStatus(saga);
    }



    private void sendInventoryReserveRequest(Order order, List<OrderItem> currentItems) {
        InventoryRequestMessage invMsg = InventoryRequestMessage.builder()
                .orderCode(order.getOrderCode())
                .sagaId(order.getCurrentSagaId())
                .warehouseCode(order.getWarehouseCode())
                .deliveryMethod(order.getDeliveryMethod().name())
                .items(currentItems.stream().map(item -> InventoryRequestMessage.InventoryItem.builder()
                        .skuCode(item.getSkuCode())
                        .quantity(item.getQuantity())
                        .isPreorder(item.getIsPreorder())
                        .build()).collect(Collectors.toList()))
                .build();
        insertOutbox(order.getCurrentSagaId(), OrderEventType.INVENTORY_RESERVE_REQ, invMsg);
    }

    private void sendVoucherApplyRequest(Order order) {
        VoucherRequestMessage vchMsg = VoucherRequestMessage.builder()
                .orderCode(order.getOrderCode())
                .orderId(order.getId())
                .sagaId(order.getCurrentSagaId())
                .voucherCode(order.getVoucherCode())
                .customerId(order.getCustomerId())
                .guestEmail(order.getReceiverEmail())
                .totalAmount(order.getTotalAmount())
                .build();
        insertOutbox(order.getCurrentSagaId(), OrderEventType.VOUCHER_APPLY_REQ, vchMsg);
    }

    private void createPaymentRequestOutbox(Order order) {
        String clientIp = order.getClientIp() != null ? order.getClientIp() : "127.0.0.1";
        String paymentType = order.getTotalRequiredNow().compareTo(order.getFinalAmount()) < 0 ? "DEPOSIT" : "FULL";

        PaymentRequestMessage payMsg = PaymentRequestMessage.builder()
                .sagaId(order.getCurrentSagaId())
                .orderCode(order.getOrderCode())
                .amount(order.getTotalRequiredNow())
                .orderInfo("Thanh toan don hang " + order.getOrderCode())
                .orderType("other")
                .locale("vn")
                .clientIpAddress(clientIp)
                .paymentMethod(order.getPaymentMethod().name())
                .paymentType(paymentType)
                .expireMinutes(timeout)
                .build();

        insertOutbox(order.getCurrentSagaId(), OrderEventType.PAYMENT_URL_REQ, payMsg);
    }


    private void handleFailedSaga(Order order, String reason) {
        log.warn("Saga failed for Order {}: {}. Triggering compensation...", order.getOrderCode(), reason);
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setStaffNote(reason);
        orderMapper.updateAllFields(order);

        updateSagaStatus(order.getCurrentSagaId(), SagaStep.COMPENSATING, SagaStatus.STARTED);
        triggerCompensation(order);
    }

    private void triggerCompensation(Order order) {
        OrderSagaInstance saga = sagaMapper.findById(order.getCurrentSagaId());
        String currentStep = saga.getCurrentStep();


        if (shouldCompensateVoucher(currentStep) && order.getVoucherCode() != null) {
            VoucherRequestMessage vchMsg = VoucherRequestMessage.builder()
                    .orderCode(order.getOrderCode())
                    .sagaId(order.getCurrentSagaId())
                    .voucherCode(order.getVoucherCode())
                    .customerId(order.getCustomerId())
                    .build();
            insertOutbox(order.getCurrentSagaId(), OrderEventType.VOUCHER_COMPENSATE_REQ, vchMsg);
        }


        if (shouldCompensateInventory(currentStep)) {
            List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
            InventoryRequestMessage invMsg = InventoryRequestMessage.builder()
                    .orderCode(order.getOrderCode())
                    .sagaId(order.getCurrentSagaId())
                    .warehouseCode(order.getWarehouseCode())
                    .items(items.stream().map(item -> InventoryRequestMessage.InventoryItem.builder()
                            .skuCode(item.getSkuCode())
                            .quantity(item.getQuantity())
                            .build()).collect(Collectors.toList()))
                    .build();
            insertOutbox(order.getCurrentSagaId(), OrderEventType.INVENTORY_COMPENSATE_REQ, invMsg);
        }

        saga.setStatus(SagaStatus.COMPENSATED);
        sagaMapper.updateStepAndStatus(saga);

        notificationClient.sendOrderUpdate(order.getOrderCode(), STATUS_FAIL);
    }
    private boolean shouldCompensateVoucher(String currentStep) {
        return currentStep.equals(SagaStep.VOUCHER_APPLYING.name()) ||
                currentStep.equals(SagaStep.PAYMENT_URL_GENERATING.name());
    }

    private boolean shouldCompensateInventory(String currentStep) {
        return currentStep.equals(SagaStep.INVENTORY_RESERVING.name()) ||
                currentStep.equals(SagaStep.VOUCHER_APPLYING.name()) ||
                currentStep.equals(SagaStep.PAYMENT_URL_GENERATING.name());
    }
}