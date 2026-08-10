package com.gsvn.inventoryservice.queue;


import com.gsvn.inventoryservice.config.RabbitMQConfig;
import com.gsvn.inventoryservice.mapper.MessageLogMapper;
import com.gsvn.inventoryservice.model.entity.Inbox;
import com.gsvn.inventoryservice.model.saga.OrderEventType;
import com.gsvn.inventoryservice.queue.message.InventoryRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryMessageListener {

    private final MessageLogMapper logMapper;
    private final ObjectMapper objectMapper;

    //(17)
    @RabbitListener(queues = RabbitMQConfig.INV_RESERVE_REQ_QUEUE)
    public void handleInventoryReserveRequest(InventoryRequestMessage message) {
        log.info("Received Inventory Reserve Request for Order: {}", message.getOrderCode());
        saveToInbox(message.getSagaId(), OrderEventType.INVENTORY_RESERVE_REQ.name(), message);
    }

    @RabbitListener(queues = RabbitMQConfig.INV_COMPENSATE_REQ_QUEUE)
    public void handleInventoryCompensateRequest(InventoryRequestMessage message) {
        log.info("Received Inventory Compensate Request for Order: {}", message.getOrderCode());
        saveToInbox(message.getSagaId(), OrderEventType.INVENTORY_COMPENSATE_REQ.name(), message);
    }

    private void saveToInbox(String eventId, String eventType, Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            Inbox inbox = Inbox.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(eventType)
                    .payload(jsonPayload)
                    .status("PENDING")
                    .build();

            logMapper.insertInbox(inbox);
            log.info("Successfully persisted {} to Inbox for Saga: {}", eventType, eventId);

        } catch (Exception e) {
            log.error("Failed to save Inbox for Event: {}", eventType, e);
            throw new RuntimeException("Database error, requeueing message...");
        }
    }
}