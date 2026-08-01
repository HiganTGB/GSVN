package com.gsvn.promotionservice.queue;


import com.gsvn.promotionservice.config.RabbitMQConfig;
import com.gsvn.promotionservice.mapper.MessageLogMapper;
import com.gsvn.promotionservice.model.entity.Inbox;
import com.gsvn.promotionservice.queue.message.VoucherRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoucherMessageListener {

    private final MessageLogMapper logMapper;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.VOUCHER_APPLY_REQ_QUEUE)
    public void handleVoucherApplyRequest(VoucherRequestMessage message) {
        log.info("Received Voucher Apply Request for Order: {}", message.getOrderCode());
        saveToInbox(message.getSagaId(), "VOUCHER_APPLY_REQ", message);
    }

    @RabbitListener(queues = RabbitMQConfig.VOUCHER_COMPENSATE_REQ_QUEUE)
    public void handleVoucherCompensateRequest(VoucherRequestMessage message) {
        log.info("Received Voucher Compensate Request for Order: {}", message.getOrderCode());
        saveToInbox(message.getSagaId(), "VOUCHER_COMPENSATE_REQ", message);
    }

    private void saveToInbox(String sagaId, String eventType, Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            Inbox inbox = Inbox.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(eventType)
                    .payload(jsonPayload)
                    .status("PENDING")
                    .build();

            logMapper.insertInbox(inbox);
            log.info("Voucher {} event saved to Inbox for Saga: {}", eventType, sagaId);

        } catch (Exception e) {
            log.error("Failed to save Voucher Inbox for Event: {}", eventType, e);
            throw new RuntimeException("Database error, requeueing message...");
        }
    }
}