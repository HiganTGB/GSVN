package com.gsvn.paymentservice.queue;

import com.gsvn.paymentservice.mapper.MessageLogMapper;
import com.gsvn.paymentservice.model.entity.Inbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentInboxWorker {

    private final MessageLogMapper logMapper;
    private final PaymentInboxProcessor inboxProcessor;

    @Scheduled(fixedDelay = 7000)
    public void processPaymentInbox() {

        List<Inbox> pendingMessages = logMapper.findPendingInbox(20);

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.debug("Found {} pending payment messages in Inbox.", pendingMessages.size());

        for (Inbox inbox : pendingMessages) {
            try {
                inboxProcessor.execute(inbox);

            } catch (Exception e) {
                log.error("Critical error processing Payment Inbox {}: {}",
                        inbox.getEventId(), e.getMessage());
                logMapper.updateInboxStatus(inbox.getEventId(), "FAIL", e.getMessage());
            }
        }
    }
}