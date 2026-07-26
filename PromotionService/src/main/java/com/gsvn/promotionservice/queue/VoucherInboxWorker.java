package com.gsvn.promotionservice.queue;

import com.gsvn.promotionservice.mapper.MessageLogMapper;
import com.gsvn.promotionservice.model.entity.Inbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoucherInboxWorker {

    private final MessageLogMapper logMapper;
    private final VoucherInboxProcessor inboxProcessor;
    @Scheduled(fixedDelay = 2000)
    public void processVoucherInbox() {
        List<Inbox> pendingMessages = logMapper.findPendingInbox(20);

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.debug("Found {} pending voucher messages in Inbox.", pendingMessages.size());

        for (Inbox inbox : pendingMessages) {
            try {
                inboxProcessor.execute(inbox);
                logMapper.updateInboxStatus(inbox.getEventId(), "PROCESSED", null);
                log.info("Successfully processed Voucher Inbox: {} - Event: {}",
                        inbox.getEventId(), inbox.getEventType());

            } catch (Exception e) {
                log.error("Failed to process Voucher Inbox: {}. Error: {}",
                        inbox.getEventId(), e.getMessage(), e);
                logMapper.updateInboxStatus(inbox.getEventId(), "FAIL", e.getMessage());
            }
        }
    }
}