package com.gsvn.paymentservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsvn.paymentservice.common.ApiResponse;
import com.gsvn.paymentservice.mapper.MessageLogMapper;
import com.gsvn.paymentservice.model.entity.Outbox;
import com.gsvn.paymentservice.model.saga.OrderEventType;
import com.gsvn.paymentservice.queue.message.PaymentCompletedMessage;
import com.gsvn.paymentservice.queue.message.PaymentRequestMessage;
import com.gsvn.paymentservice.service.VNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test/payment")
@RequiredArgsConstructor
public class DevPaymentController {

    private final MessageLogMapper logMapper;
    private final ObjectMapper mapper;
    private final VNPayService vnpayService;

    @PostMapping("/fake-success/{orderCode}")
    public ApiResponse<String> fakeSuccess(
            @PathVariable String orderCode,
            @RequestParam String sagaId,
            @RequestParam(defaultValue = "100000") BigDecimal amount) throws JsonProcessingException {


        PaymentCompletedMessage msg = PaymentCompletedMessage.builder()
                .orderCode(orderCode)
                .sagaId(sagaId)
                .status("SUCCESS")
                .amountPaid(amount)
                .gatewayResponseCode("00")
                .build();

        Outbox outbox = Outbox.builder()
                .id(UUID.randomUUID().toString())
                .aggregateId(sagaId)
                .eventType(OrderEventType.PAYMENT_COMPLETED_EVENT.name())
                .payload(mapper.writeValueAsString(msg))
                .status("PENDING")
                .retryCount(0)
                .build();

        logMapper.insertOutbox(outbox);

        return new ApiResponse<>("Fake payment SUCCESS outbox created");
    }
    @PostMapping("/fake-fail/{orderCode}")
    public ApiResponse<String> fakeFail(
            @PathVariable String orderCode,
            @RequestParam String sagaId,
            @RequestParam(defaultValue = "99") String errorCode) throws JsonProcessingException {

        PaymentCompletedMessage msg = PaymentCompletedMessage.builder()
                .orderCode(orderCode)
                .sagaId(sagaId)
                .status("FAILED")
                .amountPaid(BigDecimal.ZERO)
                .gatewayResponseCode(errorCode)
                .build();

        Outbox outbox = Outbox.builder()
                .id(UUID.randomUUID().toString())
                .aggregateId(sagaId)
                .eventType(OrderEventType.PAYMENT_FAILED_EVENT.name())
                .payload(mapper.writeValueAsString(msg))
                .status("PENDING")
                .retryCount(0)
                .build();

        logMapper.insertOutbox(outbox);

        return new ApiResponse<>( "Fake payment FAILURE outbox created");
    }
    @GetMapping("/gen")
    public String testGenUrl() {
        PaymentRequestMessage msg = PaymentRequestMessage.builder()
                .orderCode("TESTORD")
                .amount(new BigDecimal("50000"))
                .clientIpAddress("127.0.0.1")
                .build();
        return vnpayService.createPaymentUrl(msg).getCheckoutUrl();
    }

}