package com.gsvn.productservice.queue;


import com.gsvn.productservice.mapper.MessageLogMapper;
import com.gsvn.productservice.model.entity.Outbox;
import com.gsvn.productservice.queue.message.SkuValidateResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductOutboxWorker {

    private final MessageLogMapper logMapper;
    private final ProductMessagePublisher publisher;
    private final ObjectMapper objectMapper;

    //(9)
    @Scheduled(fixedDelay = 7000)
    public void processOutbox() {
        List<Outbox> pendingMessages = logMapper.findPendingOutbox(20);

        for (Outbox outbox : pendingMessages) {
            try {

                if ("SKU_VALIDATE_RES".equals(outbox.getEventType())) {
                    SkuValidateResponseMessage responseMessage = objectMapper.readValue(
                            outbox.getPayload(),
                            SkuValidateResponseMessage.class
                    );
                    publisher.sendSkuValidateResponse(responseMessage);
                }

                logMapper.updateOutboxStatus(outbox.getId(), "SENT", outbox.getRetryCount());

            } catch (Exception e) {
                log.error("Failed to send outbox message: {}. ID: {}", e.getMessage(), outbox.getId());
                int newRetryCount = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;
                String status = (newRetryCount >= 5) ? "FAILED_PERMANENTLY" : "FAILED";
                logMapper.updateOutboxStatus(outbox.getId(), status, newRetryCount);
            }
        }
    }
}