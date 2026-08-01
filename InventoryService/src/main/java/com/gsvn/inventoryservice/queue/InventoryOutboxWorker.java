package com.gsvn.inventoryservice.queue;


import com.gsvn.inventoryservice.mapper.MessageLogMapper;
import com.gsvn.inventoryservice.model.entity.Outbox;
import com.gsvn.inventoryservice.model.saga.OrderEventType;
import com.gsvn.inventoryservice.queue.message.InventoryResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryOutboxWorker {
    private final MessageLogMapper logMapper;
    private final InventoryMessagePublisher publisher;
    private final ObjectMapper mapper;
    //(20)
    @Scheduled(fixedDelay = 2000)
    public void processOutbox() {
        List<Outbox> pendings = logMapper.findPendingOutbox(20);

        for (Outbox outbox : pendings) {
            try {
                if (OrderEventType.INVENTORY_RESERVE_RES.name().equals(outbox.getEventType())) {
                    InventoryResponseMessage msg = mapper.readValue(outbox.getPayload(), InventoryResponseMessage.class);
                    publisher.sendReserveResponse(msg);
                    logMapper.updateOutboxStatus(outbox.getId(), "SENT", outbox.getRetryCount());
                }
            } catch (Exception e) {
                int retries = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;
                logMapper.updateOutboxStatus(outbox.getId(), "FAILED", retries);
            }
        }
    }
}