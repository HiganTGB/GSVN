package com.gsvn.paymentservice.controller;

import com.gsvn.paymentservice.common.ApiResponse;
import com.gsvn.paymentservice.common.PageResponse;
import com.gsvn.paymentservice.model.dto.VNPayResponse;
import com.gsvn.paymentservice.model.dto.request.PaymentRequest;
import com.gsvn.paymentservice.model.entity.PaymentTransaction;
import com.gsvn.paymentservice.service.PaymentService;
import com.gsvn.paymentservice.service.VNPayService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Management", description = "Endpoints for handling payment gateways (VNPay), transaction history, and internal Cash-on-Delivery (COD) confirmations")
public class PaymentController {

    private final VNPayService vnpayService;
    private final PaymentService paymentService;

    @Operation(summary = "VNPay IPN Webhook", description = "Server-to-server endpoint for VNPay Instant Payment Notification (IPN). Validates the checksum signature and updates transaction/order status asynchronously.")
    @GetMapping("/vnpay-ipn")
    public VNPayResponse paymentIPN(
            @Parameter(description = "All query parameters sent by VNPay webhook including vnp_SecureHash")
            @RequestParam Map<String, String> allParams) {
        log.info("VNPay IPN Called with params: {}", allParams);
        return vnpayService.processIPN(allParams);
    }

    @Operation(summary = "Get transaction history", description = "Retrieves a paginated list of payment transactions (VNPay, COD) with optional keyword filtering.")
    @GetMapping("/history")
    public ApiResponse<PageResponse<PaymentTransaction>> getHistory(
            @Parameter(description = "Keyword to filter transactions")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {

        var result = paymentService.getTransactions(keyword, null, null, "created_at", "DESC", page, size);
        return new ApiResponse<>(result);
    }

    @Operation(summary = "Confirm COD payment (Internal)", description = "Internal endpoint invoked by Order/Shipment services to mark a Cash on Delivery (COD) payment as completed when the package is successfully delivered.")
    @PostMapping("/internal/confirm-cod")
    public ApiResponse<Boolean> confirmCod(
            @RequestBody PaymentRequest request) {
        paymentService.confirmCodPayment(request);
        return new ApiResponse<>(true);
    }
}