package com.gsvn.inventoryservice.queue;

import com.gsvn.inventoryservice.config.RabbitMQConfig;
import com.gsvn.inventoryservice.queue.message.InventoryResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryMessagePublisher {
    private final RabbitTemplate rabbitTemplate;
    //(21)
    public void sendReserveResponse(InventoryResponseMessage message) {
        log.info("Sending Inventory Response for Order: {} - Success: {}",
                message.getOrderCode(), message.isSuccess());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.INVENTORY_EXCHANGE,
                RabbitMQConfig.INV_RESERVE_RES_ROUTING_KEY,
                message
        );
    }
}