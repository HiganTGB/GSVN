package com.gsvn.paymentservice.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsvn.paymentservice.exc.AppException;
import com.gsvn.paymentservice.exc.ErrorCode;
import com.gsvn.paymentservice.model.entity.*;
import com.gsvn.paymentservice.mapper.MessageLogMapper;
import com.gsvn.paymentservice.model.entity.PaymentStatus;
import com.gsvn.paymentservice.model.saga.OrderEventType;
import com.gsvn.paymentservice.queue.message.PaymentCompletedMessage;
import com.gsvn.paymentservice.queue.message.PaymentRequestMessage;
import com.gsvn.paymentservice.queue.message.PaymentResponseMessage;
import com.gsvn.paymentservice.service.PaymentService;
import com.gsvn.paymentservice.service.VNPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentInboxProcessor {

    private static final String STATUS_PROCESSED = "PROCESSED";
    private static final String STATUS_FAIL = "FAIL";
    private static final String STATUS_SUCCESS = "SUCCESS";

    // Các loại Event bắn ra Outbox
    private static final String EVENT_PAYMENT_RES = "PAYMENT_URL_RES";
    private static final String EVENT_PAYMENT_COMPLETED = "PAYMENT_COMPLETED_EVENT";

    private final VNPayService vnpayService;
    private final PaymentService paymentService;
    private final MessageLogMapper logMapper;
    private final ObjectMapper mapper;

    @Transactional
    public void execute(Inbox inbox) {
        if (STATUS_PROCESSED.equals(inbox.getStatus())) return;

        PaymentRequestMessage req = null;
        try {
            req = parsePayload(inbox.getPayload());
            processPaymentRequest(req);

            logMapper.updateInboxStatus(inbox.getEventId(), STATUS_PROCESSED, null);
            log.info("Successfully processed payment request for Order: {}", req.getOrderCode());

        } catch (AppException e) {
            log.error("Business Error: {}", e.getErrorCode().getMessage());
            handleProcessingError(inbox, req, e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("System Error: {}", e.getMessage());
            handleProcessingError(inbox, req, "Internal Server Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void processPaymentRequest(PaymentRequestMessage req) {
        PaymentTransaction transaction;
        var method = PaymentMethod.valueOf( req.getPaymentMethod());

        switch (method) {
            case VNPAY:
                transaction = vnpayService.createPaymentUrl(req);
                sendResponse(req, transaction, STATUS_SUCCESS, "URL Generated");
                break;
            case CASH:
                transaction = paymentService.saveTransaction(req, PaymentStatus.SUCCESS);
                sendResponse(req, transaction, STATUS_SUCCESS, "Cash Payment Confirmed");
                sendPaymentCompleted(req, transaction);
                break;
            default:
                throw new AppException(ErrorCode.NOT_ALLOW);
        }
    }

    // --- HELPER METHODS ---

    private PaymentRequestMessage parsePayload(String payload) {
        try {
            return mapper.readValue(payload, PaymentRequestMessage.class);
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY);
        }
    }

    private void sendResponse(PaymentRequestMessage req, PaymentTransaction tx, String status, String message) {
        try {
            PaymentResponseMessage res = PaymentResponseMessage.builder()
                    .sagaId(req.getSagaId())
                    .orderCode(req.getOrderCode())
                    .referenceId(tx != null ? tx.getReferenceId() : null)
                    .checkoutUrl(tx != null ? tx.getCheckoutUrl() : null)
                    .paymentMethod(req.getPaymentMethod())
                    .status(status)
                    .message(message)
                    .build();

            saveToOutbox(req.getSagaId(), OrderEventType.PAYMENT_URL_RES.name(), res);
        } catch (Exception e) {
            log.error("Error creating Payment Response Outbox", e);
        }
    }

    private void sendPaymentCompleted(PaymentRequestMessage req, PaymentTransaction tx) {
        try {
            PaymentCompletedMessage completedMsg = PaymentCompletedMessage.builder()
                    .sagaId(req.getSagaId())
                    .orderCode(req.getOrderCode())
                    .referenceId(tx.getReferenceId())
                    .status(STATUS_SUCCESS)
                    .amountPaid(req.getAmount())
                    .paymentMethod(req.getPaymentMethod())
                    .paymentType(req.getPaymentType())
                    .completionTime(OffsetDateTime.now())
                    .build();

            saveToOutbox(req.getSagaId(),OrderEventType.PAYMENT_COMPLETED_EVENT.name(), completedMsg);
        } catch (Exception e) {
            log.error("Error creating Payment Completed Outbox", e);
        }
    }

    private void saveToOutbox(String sagaId, String eventType, Object payload) throws JsonProcessingException {
        Outbox outbox = Outbox.builder()
                .id(UUID.randomUUID().toString())
                .aggregateId(sagaId)
                .eventType(eventType)
                .payload(mapper.writeValueAsString(payload))
                .status("PENDING")
                .build();
        logMapper.insertOutbox(outbox);
    }

    private void handleProcessingError(Inbox inbox, PaymentRequestMessage req, String errorMsg) {
        logMapper.updateInboxStatus(inbox.getEventId(), STATUS_FAIL, errorMsg);
        if (req != null) {
            sendResponse(req, null, STATUS_FAIL, errorMsg);
        }
    }
}