package com.gsvn.productservice.queue;

import com.gsvn.productservice.mapper.MessageLogMapper;
import com.gsvn.productservice.model.entity.Inbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductInboxWorker {

    private final MessageLogMapper messageLogMapper;
    private final ProductInboxProcessor productInboxProcessor;
    //(7)
    @Scheduled(fixedDelay = 7000)
    public void watchInbox() {
        List<Inbox> pendingMessages = messageLogMapper.findPendingInbox(10);

        if (pendingMessages.isEmpty()) {
            return;
        }
        log.info("Found {} pending messages in Inbox. Processing...", pendingMessages.size());

        for (Inbox inbox : pendingMessages) {
            try {
                productInboxProcessor.execute(inbox);
            } catch (Exception e) {
                log.error("Failed to process inbox message: {}", inbox.getEventId(), e);
            }
        }
    }
}