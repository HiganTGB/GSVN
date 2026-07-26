package com.gsvn.promotionservice.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsvn.promotionservice.mapper.MessageLogMapper;
import com.gsvn.promotionservice.model.entity.Outbox;
import com.gsvn.promotionservice.queue.VoucherMessagePublisher;
import com.gsvn.promotionservice.queue.message.VoucherResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoucherOutboxWorker {

    private final MessageLogMapper logMapper;
    private final VoucherMessagePublisher publisher;
    private final ObjectMapper mapper;

    @Scheduled(fixedDelay = 2000)
    public void processOutbox() {
        List<Outbox> pendingPayloads = logMapper.findPendingOutbox(20);

        for (Outbox outbox : pendingPayloads) {
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

            } catch (Exception e) {
                log.error("Failed to publish outbox message {}: {}", outbox.getId(), e.getMessage());
                int nextRetry = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;

                String status = (nextRetry > 5) ? "FAILED_PERMANENT" : "PENDING";
                logMapper.updateOutboxStatus(outbox.getId(), status, nextRetry);
            }
        }
    }
}