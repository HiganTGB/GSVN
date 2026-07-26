package com.gsvn.orderservice.queue;

import com.gsvn.orderservice.mapper.MessageLogMapper;
import com.gsvn.orderservice.model.entity.Outbox;
import com.gsvn.orderservice.model.saga.OrderEventType;
import com.gsvn.orderservice.queue.message.InventoryRequestMessage;
import com.gsvn.orderservice.queue.message.PaymentRequestMessage;
import com.gsvn.orderservice.queue.message.SkuValidateRequestMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsvn.orderservice.queue.message.VoucherRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderOutboxWorker {

    private final MessageLogMapper logMapper;
    private final OrderMessagePublisher publisher;
    private final ObjectMapper mapper;


    @Scheduled(fixedDelay = 7000)
    public void processOutbox() {

        List<Outbox> messages = logMapper.findPendingOutbox(20);

        for (Outbox outbox : messages) {
            try {
                log.debug("Processing outbox event: {} for aggregate: {}", outbox.getEventType(), outbox.getAggregateId());

                dispatchMessage(outbox);


                logMapper.updateOutboxStatus(outbox.getId(), "SENT", outbox.getRetryCount());

            } catch (Exception e) {
                log.error("Failed to process outbox {}: {}", outbox.getId(), e.getMessage());
                int retries = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;

                String status = (retries >= 5) ? "ERROR_FINAL" : "FAILED";
                logMapper.updateOutboxStatus(outbox.getId(), status, retries);
            }
        }
    }

    private void dispatchMessage(Outbox outbox) throws Exception {
        String eventType = outbox.getEventType();
        String payload = outbox.getPayload();

        //(2)
        if (OrderEventType.SKU_VALIDATE_REQ.name().equals(eventType)) {
            SkuValidateRequestMessage msg = mapper.readValue(payload, SkuValidateRequestMessage.class);
            publisher.sendSkuValidateRequest(msg);
        }
        //(15)
        else if (OrderEventType.INVENTORY_RESERVE_REQ.name().equals(eventType)) {
            InventoryRequestMessage msg = mapper.readValue(payload, InventoryRequestMessage.class);
            publisher.sendInventoryReserveRequest(msg);
        }   //(24)
        else if (OrderEventType.VOUCHER_APPLY_REQ.name().equals(eventType)) {
            VoucherRequestMessage msg = mapper.readValue(payload, VoucherRequestMessage.class);
            publisher.sendVoucherApplyRequest(msg);
        }

        else if (OrderEventType.PAYMENT_URL_REQ.name().equals(eventType)) {
            PaymentRequestMessage msg =
                    mapper.readValue(payload, PaymentRequestMessage.class);
            publisher.sendPaymentRequest(msg);
        }
        else {
            log.warn("No handler found for event type: {}", eventType);
        }
    }
}