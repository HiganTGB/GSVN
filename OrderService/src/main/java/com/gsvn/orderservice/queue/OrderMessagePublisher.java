package com.gsvn.orderservice.queue;

import com.gsvn.orderservice.config.RabbitMQConfig;
import com.gsvn.orderservice.queue.message.InventoryRequestMessage;
import com.gsvn.orderservice.queue.message.PaymentRequestMessage;
import com.gsvn.orderservice.queue.message.SkuValidateRequestMessage;
import com.gsvn.orderservice.queue.message.VoucherRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    //(3)
    public void sendSkuValidateRequest(SkuValidateRequestMessage message) {
        log.info("Sending SKU validation request for Order: {}", message.getOrderCode());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.SKU_VALIDATE_REQ_KEY,
                message
        );
    }
    //(21)
    public void sendInventoryReserveRequest(InventoryRequestMessage message) {
        log.info("Sending Inventory Reservation request for Order: {}", message.getOrderCode());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.INVENTORY_EXCHANGE,
                RabbitMQConfig.INV_RESERVE_REQ_KEY,
                message
        );
    }
    //(25)
    public void sendVoucherApplyRequest(VoucherRequestMessage message) {
        log.info("Sending Voucher Apply request for Order: {} with Voucher ID: {}",
                message.getOrderCode(), message.getVoucherCode());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.VOUCHER_EXCHANGE,
                RabbitMQConfig.VOUCHER_APPLY_REQ_KEY,
                message
        );
    }

    public void sendPaymentRequest(PaymentRequestMessage message) {
        log.info("Sending Payment URL request for Order: {}", message.getOrderCode());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_REQ_KEY,
                message
        );
    }
}