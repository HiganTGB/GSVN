package com.gsvn.paymentservice.controller;

import com.gsvn.paymentservice.common.ApiResponse;
import com.gsvn.paymentservice.common.PageResponse;
import com.gsvn.paymentservice.model.dto.VNPayResponse;
import com.gsvn.paymentservice.model.dto.request.PaymentRequest;
import com.gsvn.paymentservice.model.entity.PaymentTransaction;
import com.gsvn.paymentservice.service.PaymentService;
import com.gsvn.paymentservice.service.VNPayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final VNPayService vnpayService;
    private final PaymentService paymentService;

    @GetMapping("/vnpay-ipn")
    public VNPayResponse paymentIPN(@RequestParam Map<String, String> allParams) {
        log.info("VNPay IPN Called with params: {}", allParams);
        return vnpayService.processIPN(allParams);
    }

    @GetMapping("/history")
    public ApiResponse<PageResponse<PaymentTransaction>> getHistory(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        var result = paymentService.getTransactions(keyword, null, null, "created_at", "DESC", page, size);
        return new ApiResponse<>(result);
    }
    @PostMapping("/internal/confirm-cod")
    public ApiResponse<Boolean> confirmCod(
            @RequestBody PaymentRequest request) {
        paymentService.confirmCodPayment(request);
        return new ApiResponse<>(true);
    }
}