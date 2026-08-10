package com.gsvn.productservice.queue;


import com.gsvn.productservice.mapper.MessageLogMapper;
import com.gsvn.productservice.model.entity.Outbox;
import com.gsvn.productservice.queue.message.SkuValidateResponseMessage;
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
public class ProductOutboxWorker {

    private final MessageLogMapper logMapper;
    private final ProductMessagePublisher publisher;
    private final ObjectMapper objectMapper;

    private static final int MAX_RETRIES = 5;
    private static final long BASE_BACKOFF_MS = 2000L;
    //(9)
    @Scheduled(fixedDelay = 7000)
    public void processOutbox() {
        List<Outbox> pendingMessages = logMapper.findPendingOutbox(20);

        if (pendingMessages.isEmpty()) {
            return;
        }

        for (Outbox outbox : pendingMessages) {
            if (!shouldProcessMessage(outbox)) {
                continue;
            }
            try {
                if ("SKU_VALIDATE_RES".equals(outbox.getEventType())) {
                    SkuValidateResponseMessage responseMessage = objectMapper.readValue(
                            outbox.getPayload(),
                            SkuValidateResponseMessage.class
                    );
                    publisher.sendSkuValidateResponse(responseMessage);
                } else {
                    log.warn("No handler found for event type: {}", outbox.getEventType());
                }
                logMapper.updateOutboxStatus(outbox.getId(), "SENT", outbox.getRetryCount());

            } catch (CallNotPermittedException e) {
                int newRetryCount = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;
                String status = (newRetryCount >= MAX_RETRIES) ? "FAILED_PERMANENTLY" : "FAILED";

                log.warn("[PRODUCT OUTBOX WORKER] Circuit Breaker OPEN for RabbitMQ! Record ID: {} increased retries to {}/{}. Aborting current batch scan.",
                        outbox.getId(), newRetryCount, MAX_RETRIES);

                logMapper.updateOutboxStatus(outbox.getId(), status, newRetryCount);
                break;

            } catch (Exception e) {
                int newRetryCount = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;
                String status = (newRetryCount >= MAX_RETRIES) ? "FAILED_PERMANENTLY" : "FAILED";

                log.error("Failed to send outbox message ID: {} (Attempt {}/{}): {}",
                        outbox.getId(), newRetryCount, MAX_RETRIES, e.getMessage());

                logMapper.updateOutboxStatus(outbox.getId(), status, newRetryCount);
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