package com.gsvn.paymentservice.queue;

import com.gsvn.paymentservice.mapper.MessageLogMapper;
import com.gsvn.paymentservice.model.entity.Outbox;
import com.gsvn.paymentservice.queue.message.PaymentCompletedMessage;
import com.gsvn.paymentservice.queue.message.PaymentResponseMessage;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentOutboxWorker {

    private final MessageLogMapper logMapper;
    private final PaymentMessagePublisher publisher;
    private final ObjectMapper mapper;

    private static final int MAX_RETRIES = 5;
    private static final long BASE_BACKOFF_MS = 2000L;

    @Scheduled(fixedDelay = 7000)
    public void processPaymentOutbox() {
        List<Outbox> pendingPayloads = logMapper.findPendingOutbox(20);

        if (pendingPayloads.isEmpty()) {
            return;
        }

        for (Outbox outbox : pendingPayloads) {
            if (!shouldProcessMessage(outbox)) {
                continue;
            }

            try {
                if ("PAYMENT_URL_RES".equals(outbox.getEventType())) {
                    PaymentResponseMessage res = mapper.readValue(outbox.getPayload(), PaymentResponseMessage.class);
                    publisher.sendPaymentResponse(res);
                }
                else if ("PAYMENT_COMPLETED_EVENT".equals(outbox.getEventType())) {
                    PaymentCompletedMessage res = mapper.readValue(outbox.getPayload(), PaymentCompletedMessage.class);
                    publisher.sendPaymentCompletedEvent(res);
                }
                else {
                    log.warn("Unknown event type in payment outbox: {}", outbox.getEventType());
                }
                logMapper.updateOutboxStatus(outbox.getId(), "SENT", outbox.getRetryCount());

            } catch (CallNotPermittedException e) {
                int nextRetry = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;
                String status = (nextRetry > MAX_RETRIES) ? "FAILED_PERMANENT" : "FAILED";
                log.warn("[PAYMENT OUTBOX WORKER] Circuit Breaker OPEN for RabbitMQ! Record ID: {} increased retries to {}/{}. Aborting current batch scan.",
                        outbox.getId(), nextRetry, MAX_RETRIES);
                logMapper.updateOutboxStatus(outbox.getId(), status, nextRetry);
                break;
            } catch (Exception e) {
                log.error("Failed to process payment outbox ID {}: {}", outbox.getId(), e.getMessage());
                int nextRetry = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;

                String status = (nextRetry > MAX_RETRIES) ? "FAILED_PERMANENT" : "FAILED";
                logMapper.updateOutboxStatus(outbox.getId(), status, nextRetry);
            }
        }
    }
    private boolean shouldProcessMessage(Outbox outbox) {
        if (outbox.getRetryCount() == null || outbox.getRetryCount() == 0) {
            return true;
        }
        if (outbox.getLastAttemptAt() == null) {
            return true;
        }

        long delayMillis = BASE_BACKOFF_MS * (long) Math.pow(2, outbox.getRetryCount() - 1);
        OffsetDateTime nextAttemptTime = outbox.getLastAttemptAt().plusNanos(delayMillis * 1_000_000L);

        return OffsetDateTime.now().isAfter(nextAttemptTime);
    }
}