package com.gsvn.promotionservice.queue;


import com.gsvn.promotionservice.mapper.MessageLogMapper;
import com.gsvn.promotionservice.model.entity.Outbox;
import com.gsvn.promotionservice.queue.VoucherMessagePublisher;
import com.gsvn.promotionservice.queue.message.VoucherResponseMessage;
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
public class VoucherOutboxWorker {

    private final MessageLogMapper logMapper;
    private final VoucherMessagePublisher publisher;
    private final ObjectMapper mapper;
    private static final int MAX_RETRIES = 5;
    private static final long BASE_BACKOFF_MS = 2000L;
    @Scheduled(fixedDelay = 2000)
    public void processOutbox() {
        List<Outbox> pendingPayloads = logMapper.findPendingOutbox(20);
        if (pendingPayloads.isEmpty()) {
            return;
        }
        for (Outbox outbox : pendingPayloads) {
            if (!shouldProcessMessage(outbox)) {
                continue;
            }
            try {
                switch (outbox.getEventType()) {
                    case "VOUCHER_APPLY_RES":
                        VoucherResponseMessage message = mapper.readValue(
                                outbox.getPayload(),
                                VoucherResponseMessage.class
                        );
                        publisher.sendVoucherResponse(message);
                        break;

                    default:
                        log.warn("Unknown event type in outbox: {}", outbox.getEventType());
                        break;
                }

                logMapper.updateOutboxStatus(outbox.getId(), "SENT", outbox.getRetryCount());

            } catch (CallNotPermittedException e) {
                int nextRetry = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;
                String status = (nextRetry > MAX_RETRIES) ? "FAILED_PERMANENT" : "FAILED";
                log.warn("[PROMOTION OUTBOX WORKER] Circuit Breaker OPEN for RabbitMQ! Record ID: {} increased retries to {}/{}. Aborting current batch scan.",
                        outbox.getId(), nextRetry, MAX_RETRIES);
                logMapper.updateOutboxStatus(outbox.getId(), status, nextRetry);
                break;

            } catch (Exception e) {
                log.error("Failed to publish outbox message {}: {}", outbox.getId(), e.getMessage());
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