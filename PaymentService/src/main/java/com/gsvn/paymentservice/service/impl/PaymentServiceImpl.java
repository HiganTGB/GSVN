package com.gsvn.paymentservice.service.impl;


import com.gsvn.paymentservice.common.PageResponse;
import com.gsvn.paymentservice.exc.AppException;
import com.gsvn.paymentservice.exc.ErrorCode;
import com.gsvn.paymentservice.mapper.MessageLogMapper;
import com.gsvn.paymentservice.mapper.PaymentTransactionMapper;
import com.gsvn.paymentservice.model.dto.request.PaymentRequest;
import com.gsvn.paymentservice.model.entity.Outbox;
import com.gsvn.paymentservice.model.entity.PaymentStatus;
import com.gsvn.paymentservice.model.entity.PaymentTransaction;
import com.gsvn.paymentservice.queue.message.PaymentCompletedMessage;
import com.gsvn.paymentservice.queue.message.PaymentRequestMessage;
import com.gsvn.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentTransactionMapper paymentMapper;
    private final MessageLogMapper messageLogMapper;
    private final ObjectMapper objectMapper;


    public PaymentTransaction saveTransaction(PaymentRequestMessage msg, PaymentStatus status) {
        String referenceId = String.format("%s_%d", msg.getOrderCode(), System.currentTimeMillis());
        PaymentTransaction tx = PaymentTransaction.builder()
                .orderCode(msg.getOrderCode())
                .referenceId(referenceId)
                .provider(msg.getPaymentMethod())
                .paymentMethod(msg.getPaymentMethod())
                .paymentType(msg.getPaymentType())
                .amount(msg.getAmount())
                .currency("VND")
                .status(status.name())
                .createdAt(OffsetDateTime.now())
                .build();
        paymentMapper.insert(tx);
        return tx;
    }

    @Transactional
    public void confirmManualPayment(Long transactionId, String adminNote) {
        PaymentTransaction tx = paymentMapper.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Giao dịch không tồn tại"));
        if ("SUCCESS".equals(tx.getStatus())) {
            throw new RuntimeException("Giao dịch này đã được xác nhận trước đó");
        }

        tx.setStatus("SUCCESS");
        tx.setNote(adminNote);
        tx.setUpdatedAt(OffsetDateTime.now());
        paymentMapper.update(tx);

        try {
            createPaymentCompletedOutbox(tx, "MANUAL_" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Lỗi khi tạo Outbox xác nhận thanh toán: ", e);
            throw new RuntimeException("Hệ thống không thể gửi thông báo thanh toán");
        }
    }


    private void createPaymentCompletedOutbox(PaymentTransaction tx, String externalTransNo) throws Exception {

        PaymentCompletedMessage payload = PaymentCompletedMessage.builder()
                .orderCode(tx.getOrderCode())
                .status("SUCCESS")
                .amountPaid(tx.getAmount())
                .paymentMethod("VNPAY")
                .paymentType(tx.getPaymentType())
                .referenceId(tx.getReferenceId())
                .externalTransactionId(externalTransNo)
                .completionTime(OffsetDateTime.now())
                .build();

        Outbox outbox = Outbox.builder()
                .id(UUID.randomUUID().toString())
                .aggregateId(tx.getOrderCode())
                .eventType("PAYMENT_COMPLETED_EVENT")
                .payload(objectMapper.writeValueAsString(payload))
                .status("PENDING")
                .build();
        messageLogMapper.insertOutbox(outbox);
    }
    public PageResponse<PaymentTransaction> getTransactions(String keyword, String status, String provider,
                                                            String sortField, String sortOrder,
                                                            int page, int size) {

        List<String> validFields = Arrays.asList("id", "created_at", "amount", "order_code");
        String finalSortField = validFields.contains(sortField) ? sortField : "created_at";
        String finalSortOrder = "ASC".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC";

        int offset = (page - 1) * size;

        List<PaymentTransaction> data = paymentMapper.findPage(
                keyword, status, provider, finalSortField, finalSortOrder, size, offset
        );

        long totalElements = paymentMapper.countSearch(keyword, status);

        return PageResponse.of(data, totalElements, page, size);
    }
    @Transactional
    public void confirmCodPayment(PaymentRequest request) {
        PaymentTransaction tx = PaymentTransaction.builder()
                .orderCode(request.getOrderCode())
                .shipmentCode(request.getShipmentCode())
                .referenceId(request.getReferenceId())
                .provider(request.getProvider())
                .paymentMethod(request.getPaymentMethod())
                .paymentType(request.getPaymentType())
                .amount(request.getAmount())
                .currency("VND")
                .status(PaymentStatus.SUCCESS.name())
                .createdAt(OffsetDateTime.now())
                .build();
        paymentMapper.insert(tx);
        try {
            createPaymentCompletedOutbox(tx,request.getExternalTransactionId());
        }catch (Exception ex)
        {
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY);
        }

    }
}