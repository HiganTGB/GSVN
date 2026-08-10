package com.gsvn.inventoryservice.queue;


import com.gsvn.inventoryservice.mapper.MessageLogMapper;
import com.gsvn.inventoryservice.model.entity.Outbox;
import com.gsvn.inventoryservice.model.saga.OrderEventType;
import com.gsvn.inventoryservice.queue.message.InventoryResponseMessage;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryOutboxWorker {
    private final MessageLogMapper logMapper;
    private final InventoryMessagePublisher publisher;
    private final ObjectMapper mapper;
    private static final int MAX_RETRIES = 5;
    private static final long BASE_BACKOFF_MS = 2000L;
    //(20)
    @Scheduled(fixedDelay = 2000)
    public void processOutbox() {
        List<Outbox> pendings = logMapper.findPendingOutbox(20);
        if (pendings.isEmpty()) {
            return;
        }
        for (Outbox outbox : pendings) {
            if (!shouldProcessMessage(outbox)) {
                continue;
            }
            try {
                if (OrderEventType.INVENTORY_RESERVE_RES.name().equals(outbox.getEventType())) {
                    InventoryResponseMessage msg = mapper.readValue(outbox.getPayload(), InventoryResponseMessage.class);
                    publisher.sendReserveResponse(msg);
                    logMapper.updateOutboxStatus(outbox.getId(), "SENT", outbox.getRetryCount());
                }
            } catch (CallNotPermittedException e) {
                int retries = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;
                String status = (retries >= MAX_RETRIES) ? "FAILED_PERMANENTLY" : "FAILED";

                log.warn("[INVENTORY OUTBOX WORKER] Circuit Breaker OPEN for RabbitMQ! Record ID: {} increased retries to {}/{}. Aborting current batch scan.",
                        outbox.getId(), retries, MAX_RETRIES);

                logMapper.updateOutboxStatus(outbox.getId(), status, retries);
                break;

            } catch (Exception e) {
                int retries = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;
                String status = (retries >= MAX_RETRIES) ? "FAILED_PERMANENTLY" : "FAILED";

                log.error("Failed to process inventory outbox event {} (Attempt {}/{}): {}",
                        outbox.getId(), retries, MAX_RETRIES, e.getMessage());

                logMapper.updateOutboxStatus(outbox.getId(), status, retries);
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