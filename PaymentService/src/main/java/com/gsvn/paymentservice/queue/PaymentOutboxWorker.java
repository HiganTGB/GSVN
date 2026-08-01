package com.gsvn.paymentservice.queue;



import com.gsvn.paymentservice.mapper.MessageLogMapper;
import com.gsvn.paymentservice.model.entity.Outbox;
import com.gsvn.paymentservice.queue.message.PaymentCompletedMessage;
import com.gsvn.paymentservice.queue.message.PaymentResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentOutboxWorker {

    private final MessageLogMapper logMapper;
    private final PaymentMessagePublisher publisher;
    private final ObjectMapper mapper;

    @Scheduled(fixedDelay = 7000)
    public void processPaymentOutbox() {
        List<Outbox> pendingPayloads = logMapper.findPendingOutbox(20);

        for (Outbox outbox : pendingPayloads) {
            try {
                if ("PAYMENT_URL_RES".equals(outbox.getEventType())) {
                    PaymentResponseMessage res = mapper.readValue(outbox.getPayload(), PaymentResponseMessage.class);
                    publisher.sendPaymentResponse(res);
                }
                else if ("PAYMENT_COMPLETED_EVENT".equals(outbox.getEventType())) {
                    PaymentCompletedMessage res=mapper.readValue(outbox.getPayload(),PaymentCompletedMessage.class);
                    publisher.sendPaymentCompletedEvent(res);
                }

                logMapper.updateOutboxStatus(outbox.getId(), "SENT", outbox.getRetryCount());
            } catch (Exception e) {
                log.error("Failed to process Outbox ID {}: {}", outbox.getId(), e.getMessage());
                log.error("Failed to publish payment outbox {}: {}", outbox.getId(), e.getMessage());
                int nextRetry = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;
                String status = (nextRetry > 5) ? "FAILED" : "PENDING";
                logMapper.updateOutboxStatus(outbox.getId(), status, nextRetry);
            }
        }
    }
}