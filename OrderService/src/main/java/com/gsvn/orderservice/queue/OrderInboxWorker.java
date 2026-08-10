package com.gsvn.orderservice.queue;

import com.gsvn.orderservice.mapper.MessageLogMapper;
import com.gsvn.orderservice.model.entity.Inbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderInboxWorker {

    private final MessageLogMapper logMapper;
    private final OrderInboxProcessor inboxProcessor;
    //(14) (22)
    @Scheduled(fixedDelay = 7000)
    public void processInbox() {
        List<Inbox> pendingMessages = logMapper.findPendingInbox(20);

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.debug("Found {} pending messages in Order Inbox. Processing...", pendingMessages.size());

        for (Inbox inbox : pendingMessages) {
            try {
                inboxProcessor.execute(inbox);

                log.info("Successfully processed Order Inbox message: {} - Type: {}",
                        inbox.getEventId(), inbox.getEventType());

            } catch (Exception e) {
                log.error("Failed to process Order Inbox message: {}. Reason: {}",
                        inbox.getEventId(), e.getMessage(), e);
                logMapper.updateInboxStatus(inbox.getEventId(), "FAILED", e.getMessage());
            }
        }
    }
}