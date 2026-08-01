package com.gsvn.paymentservice.service.impl;

import com.gsvn.paymentservice.config.VNPayConfig;
import com.gsvn.paymentservice.config.VNPayProperties;
import com.gsvn.paymentservice.model.entity.PaymentMethod;
import com.gsvn.paymentservice.mapper.MessageLogMapper;
import com.gsvn.paymentservice.mapper.PaymentTransactionMapper;
import com.gsvn.paymentservice.model.dto.VNPayResponse;
import com.gsvn.paymentservice.model.entity.Outbox;
import com.gsvn.paymentservice.model.entity.PaymentStatus;
import com.gsvn.paymentservice.model.entity.PaymentTransaction;
import com.gsvn.paymentservice.queue.message.PaymentRequestMessage;
import com.gsvn.paymentservice.service.VNPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayServiceImpl implements VNPayService {

    private final VNPayConfig vnpayConfig;
    private final VNPayProperties vnpayProperties;
    private final PaymentTransactionMapper paymentMapper;
    private final MessageLogMapper messageLogMapper;
    private final ObjectMapper objectMapper;


    private static final DateTimeFormatter VNP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Transactional
    public PaymentTransaction createPaymentUrl(PaymentRequestMessage message) {
        String txnRef = "PAY" + message.getOrderCode() + "T" + System.currentTimeMillis();

        Map<String, String> params = buildBasicParams(message, txnRef);
        String queryUrl = buildQueryString(params);
        String secureHash = vnpayConfig.hmacSHA512(vnpayProperties.getHashSecret(), queryUrl);
        String finalUrl = vnpayProperties.getPayUrl() + "?" + queryUrl + "&vnp_SecureHash=" + secureHash;
        return saveTransaction(message, txnRef, finalUrl);
    }

    @Transactional
    public VNPayResponse processIPN(Map<String, String> params) {
        // 1. Verify Checksum
        if (!verifyCallback(params)) return new VNPayResponse("97", "Invalid Checksum");

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        BigDecimal vnpAmount = new BigDecimal(params.get("vnp_Amount")).divide(new BigDecimal(100));

        // 2. Validate Transaction
        PaymentTransaction tx = paymentMapper.findByReferenceId(txnRef).orElse(null);
        if (tx == null) return new VNPayResponse("01", "Order not found");
        if (tx.getAmount().compareTo(vnpAmount) != 0) return new VNPayResponse("04", "Invalid Amount");
        if (!"PENDING".equals(tx.getStatus())) return new VNPayResponse("02", "Already confirmed");

        // 3. Update Status & Notify Order Service
        String status = "00".equals(responseCode) ? "SUCCESS" : "FAILED";
        updateAndNotify(tx, params, status);

        return new VNPayResponse("00", "Confirm Success");
    }

    private void updateAndNotify(PaymentTransaction tx, Map<String, String> params, String status) {
        try {
            String jsonRaw = objectMapper.writeValueAsString(params);
            paymentMapper.updateStatus(tx.getReferenceId(), status, params.get("vnp_TransactionNo"), jsonRaw);

            if ("SUCCESS".equals(status)) {
                createPaymentCompletedOutbox(tx, params.get("vnp_TransactionNo"));
            }
        } catch (Exception e) {
            log.error("IPN Update Error: ", e);
            throw new RuntimeException("IPN processing failed");
        }
    }

    private Map<String, String> buildBasicParams(PaymentRequestMessage msg, String txnRef) {
        Map<String, String> p = new HashMap<>();
        p.put("vnp_Version", vnpayProperties.getVersion());
        p.put("vnp_Command", vnpayProperties.getCommand());
        p.put("vnp_TmnCode", vnpayProperties.getTmnCode());
        p.put("vnp_Amount", String.valueOf(msg.getAmount().multiply(new BigDecimal(100)).longValue()));
        p.put("vnp_CurrCode", vnpayProperties.getCurrCode());
        p.put("vnp_TxnRef", txnRef);
        p.put("vnp_OrderInfo", "Thanh toan don hang: " + msg.getOrderCode());
        p.put("vnp_OrderType", vnpayProperties.getOrderType());
        p.put("vnp_Locale", "vn");
        p.put("vnp_ReturnUrl", vnpayProperties.getReturnUrl()+ "?orderCode=" + msg.getOrderCode());
        p.put("vnp_IpAddr", msg.getClientIpAddress());

        LocalDateTime now = LocalDateTime.now(ZoneOffset.ofHours(7));
        p.put("vnp_CreateDate", now.format(VNP_FORMATTER));
        p.put("vnp_ExpireDate", now.plusMinutes(15).format(VNP_FORMATTER));
        return p;
    }

    private PaymentTransaction saveTransaction(PaymentRequestMessage msg, String txnRef, String checkoutUrl) {
        PaymentTransaction tx = PaymentTransaction.builder()
                .orderCode(msg.getOrderCode())
                .referenceId(txnRef)
                .provider(PaymentMethod.VNPAY.name())
                .paymentMethod(msg.getPaymentMethod())
                .amount(msg.getAmount())
                .currency("VND")
                .checkoutUrl(checkoutUrl)
                .paymentType(msg.getPaymentType())
                .status(PaymentStatus.PENDING.name())
                .createdAt(OffsetDateTime.now())
                .build();
        paymentMapper.insert(tx);
        return tx;
    }

    private void createPaymentCompletedOutbox(PaymentTransaction tx, String gatewayTransNo) throws Exception {
        Map<String, Object> payload = Map.of(
                "orderCode", tx.getOrderCode(),
                "status", "SUCCESS",
                "amountPaid", tx.getAmount(),
                "paymentMethod", "VNPAY",
                "paymentType", tx.getPaymentType(),
                "referenceId", tx.getReferenceId(),
                "gatewayTransactionNo", gatewayTransNo,
                "completionTime", OffsetDateTime.now().toString()
        );

        Outbox outbox = Outbox.builder()
                .id(UUID.randomUUID().toString())
                .aggregateId(tx.getOrderCode())
                .eventType("PAYMENT_COMPLETED_EVENT")
                .payload(objectMapper.writeValueAsString(payload))
                .status("PENDING")
                .build();
        messageLogMapper.insertOutbox(outbox);
    }


    private String buildQueryString(Map<String, String> params) {
        return params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.US_ASCII) + "=" +
                        URLEncoder.encode(e.getValue(), StandardCharsets.US_ASCII))
                .collect(Collectors.joining("&"));
    }

    public boolean verifyCallback(Map<String, String> queryParams) {
        String vnp_SecureHash = queryParams.get("vnp_SecureHash");

        Map<String, String> hashData = new HashMap<>(queryParams);
        hashData.remove("vnp_SecureHash");
        hashData.remove("vnp_SecureHashType");

        String rawData = hashData.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        String checkSum = vnpayConfig.hmacSHA512(vnpayProperties.getHashSecret(), rawData);
        return checkSum.equalsIgnoreCase(vnp_SecureHash);
    }
}