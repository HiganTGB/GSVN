package com.gsvn.productservice.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsvn.productservice.config.RabbitMQConfig;
import com.gsvn.productservice.mapper.MessageLogMapper;
import com.gsvn.productservice.model.entity.Inbox;
import com.gsvn.productservice.model.saga.OrderEventType;
import com.gsvn.productservice.queue.message.SkuValidateRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductMessageListener {

    private final MessageLogMapper logMapper;
    private final ObjectMapper objectMapper;
    //(6)
    @RabbitListener(queues = RabbitMQConfig.SKU_VALIDATE_REQ_QUEUE)
    public void handleSkuValidateRequest(SkuValidateRequestMessage request) {
        log.info("Received SKU validation request for Order: {}", request.getOrderCode());

        try {
            String jsonPayload = objectMapper.writeValueAsString(request);

            Inbox inbox = Inbox.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(OrderEventType.SKU_VALIDATE_REQ.name())
                    .payload(jsonPayload)
                    .status("PENDING")
                    .build();

            logMapper.insertInbox(inbox);
            log.info("Successfully persisted message to Inbox for Order: {}", request.getOrderCode());

        } catch (Exception e) {
            log.error("CRITICAL: Failed to save Inbox for Order: {}", request.getOrderCode(), e);
            throw new RuntimeException("Database error, requeueing message...");
        }
    }
}