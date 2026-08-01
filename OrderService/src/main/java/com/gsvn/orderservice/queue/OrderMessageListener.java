package com.gsvn.orderservice.queue;


import com.gsvn.orderservice.config.RabbitMQConfig;
import com.gsvn.orderservice.config.RabbitMQTimeoutConfig;
import com.gsvn.orderservice.mapper.MessageLogMapper;
import com.gsvn.orderservice.mapper.OrderMapper;
import com.gsvn.orderservice.model.entity.Inbox;
import com.gsvn.orderservice.model.entity.Order;
import com.gsvn.orderservice.model.enums.OrderStatus;
import com.gsvn.orderservice.model.saga.OrderEventType;
import com.gsvn.orderservice.queue.message.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderMessageListener {

    private final MessageLogMapper logMapper;
    private final ObjectMapper objectMapper;
    private final OrderMapper orderMapper;
    //(13)
    @RabbitListener(queues = RabbitMQConfig.SKU_VALIDATE_RES_QUEUE)
    public void handleSkuValidateResponse(SkuValidateResponseMessage response) {
        saveToInbox(response.getOrderCode(), OrderEventType.SKU_VALIDATE_RES.name(), response);
    }

    @RabbitListener(queues = RabbitMQConfig.INV_RESERVE_RES_QUEUE)
    public void handleInventoryReserveResponse(InventoryResponseMessage response) {
        saveToInbox(response.getOrderCode(), OrderEventType.INVENTORY_RESERVE_RES.name(), response);
    }

    @RabbitListener(queues = RabbitMQConfig.VOUCHER_APPLY_RES_QUEUE)
    public void handleVoucherApplyResponse(VoucherResponseMessage response) {
        saveToInbox(response.getOrderCode(), OrderEventType.VOUCHER_APPLY_RES.name(), response);
    }
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_RES_QUEUE)
    public void handlePaymentResponse(PaymentResponseMessage response) {
        saveToInbox(response.getOrderCode(), OrderEventType.PAYMENT_URL_RES.name(), response);
    }
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_COMPLETED_RES_QUEUE)
    public void handlePaymentResponse(PaymentCompletedMessage response) {
        saveToInbox(response.getOrderCode(), OrderEventType.PAYMENT_COMPLETED_EVENT.name(), response);
    }
    @RabbitListener(queues = RabbitMQTimeoutConfig.ORDER_CANCEL_QUEUE)
    public void handleOrderTimeout(String orderCode) {
        log.info("15 minutes passed. Checking status for order: {}", orderCode);


        OrderStatus status = orderMapper.getStatusByOrderCode(orderCode);

        if (OrderStatus.VALIDATED.equals(status)) {
            saveToInbox(orderCode, OrderEventType.PAYMENT_FAILED_EVENT.name(), orderCode);
            log.info("Order {} has been cancelled due to timeout payment.", orderCode);
        } else {
            log.info("Order {} already processed or paid. No action needed.", orderCode);
        }
    }

    private void saveToInbox(String orderCode, String eventType, Object payload) {
        try {
            log.info("Received {} for Order: {}", eventType, orderCode);
            String jsonPayload = objectMapper.writeValueAsString(payload);

            Inbox inbox = Inbox.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(eventType)
                    .payload(jsonPayload)
                    .status("PENDING")
                    .build();

            logMapper.insertInbox(inbox);
        } catch (Exception e) {
            log.error("Failed to save Inbox for Order: {} - Event: {}", orderCode, eventType, e);
            throw new RuntimeException("Requeue message due to DB error");
        }
    }
}