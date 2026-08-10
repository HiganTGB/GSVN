package com.gsvn.orderservice.queue;

import com.gsvn.orderservice.mapper.MessageLogMapper;
import com.gsvn.orderservice.model.entity.Outbox;
import com.gsvn.orderservice.model.saga.OrderEventType;
import com.gsvn.orderservice.queue.message.InventoryRequestMessage;
import com.gsvn.orderservice.queue.message.PaymentRequestMessage;
import com.gsvn.orderservice.queue.message.SkuValidateRequestMessage;

import com.gsvn.orderservice.queue.message.VoucherRequestMessage;
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
public class OrderOutboxWorker {

    private final MessageLogMapper logMapper;
    private final OrderMessagePublisher publisher;
    private final ObjectMapper mapper;
    private static final int MAX_RETRIES = 5;
    private static final long BASE_BACKOFF_MS = 2000L;

    @Scheduled(fixedDelay = 7000)
    public void processOutbox() {

        List<Outbox> messages = logMapper.findPendingOutbox(20);

        for (Outbox outbox : messages) {
            if (!shouldProcessMessage(outbox)) {
                continue;
            }
            try {
                log.debug("Processing outbox event: {} for aggregate: {}", outbox.getEventType(), outbox.getAggregateId());

                dispatchMessage(outbox);


                logMapper.updateOutboxStatus(outbox.getId(), "SENT", outbox.getRetryCount());

            }catch (CallNotPermittedException e) {
                int retries = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;
                String status = (retries >= MAX_RETRIES) ? "ERROR_FINAL" : "FAILED";

                log.warn("[OUTBOX WORKER] Circuit Breaker OPEN! Record ID: {} increased retries to {}/{}. Aborting current batch scan.",
                        outbox.getId(), retries, MAX_RETRIES);

                logMapper.updateOutboxStatus(outbox.getId(), status, retries);
                break;
            }catch (Exception e) {
                int retries = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;
                String status = (retries >= MAX_RETRIES) ? "ERROR_FINAL" : "FAILED";

                log.error("Failed to process outbox event {} (Attempt {}/{}): {}",
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
    private void dispatchMessage(Outbox outbox) throws Exception {
        String eventType = outbox.getEventType();
        String payload = outbox.getPayload();

        if (OrderEventType.SKU_VALIDATE_REQ.name().equals(eventType)) {
            SkuValidateRequestMessage msg = mapper.readValue(payload, SkuValidateRequestMessage.class);
            publisher.sendSkuValidateRequest(msg);
        }
        else if (OrderEventType.INVENTORY_RESERVE_REQ.name().equals(eventType)
                || OrderEventType.INVENTORY_COMPENSATE_REQ.name().equals(eventType)) {
            InventoryRequestMessage msg = mapper.readValue(payload, InventoryRequestMessage.class);
            publisher.sendInventoryReserveRequest(msg);
        }
        else if (OrderEventType.VOUCHER_APPLY_REQ.name().equals(eventType)
                || OrderEventType.VOUCHER_COMPENSATE_REQ.name().equals(eventType)) {
            VoucherRequestMessage msg = mapper.readValue(payload, VoucherRequestMessage.class);
            publisher.sendVoucherApplyRequest(msg);
        }
        else if (OrderEventType.PAYMENT_URL_REQ.name().equals(eventType)) {
            PaymentRequestMessage msg = mapper.readValue(payload, PaymentRequestMessage.class);
            publisher.sendPaymentRequest(msg);
        }
        else {
            log.warn("No handler found for event type: {}", eventType);
        }
    }
}