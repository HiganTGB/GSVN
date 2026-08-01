package com.gsvn.paymentservice.queue;


import com.gsvn.paymentservice.config.RabbitMQConfig;
import com.gsvn.paymentservice.mapper.MessageLogMapper;
import com.gsvn.paymentservice.model.entity.Inbox;
import com.gsvn.paymentservice.queue.message.PaymentRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;


import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentMessageListener {

    private final MessageLogMapper logMapper;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_REQ_QUEUE)
    public void handlePaymentRequest(PaymentRequestMessage response) {
        log.info("Received Payment Request for Order: {}", response.getOrderCode());
        saveToInbox(response.getSagaId(), "PAYMENT_URL_REQ", response);
    }

    private void saveToInbox(String sagaId, String eventType, Object payload) {
        try {
            Inbox inbox = Inbox.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(eventType)
                    .payload(objectMapper.writeValueAsString(payload))
                    .status("PENDING")
                    .build();
            logMapper.insertInbox(inbox);
        } catch (Exception e) {
            log.error("Failed to save Inbox for Saga: {}", sagaId, e);
        }
    }
}