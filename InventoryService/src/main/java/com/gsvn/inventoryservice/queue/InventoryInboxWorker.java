package com.gsvn.inventoryservice.queue;

import com.gsvn.inventoryservice.mapper.MessageLogMapper;
import com.gsvn.inventoryservice.model.entity.Inbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryInboxWorker {

    private final MessageLogMapper logMapper;
    private final InventoryInboxProcessor inboxProcessor;
    //(18)
    @Scheduled(fixedDelay = 2000)
    public void watchInbox() {
        List<Inbox> pendingMessages = logMapper.findPendingInbox(20);

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.debug("Found {} pending messages in Inventory Inbox. Processing...", pendingMessages.size());

        for (Inbox inbox : pendingMessages) {
            try {
                inboxProcessor.execute(inbox);

                log.info("Successfully processed Inbox message: {} - Event: {}",
                        inbox.getEventId(), inbox.getEventType());

            } catch (Exception e) {
                log.error("Failed to process Inventory Inbox message: {}. Reason: {}",
                        inbox.getEventId(), e.getMessage(), e);
                logMapper.updateInboxStatus(inbox.getEventId(), "FAIL", e.getMessage());
            }
        }
    }
}