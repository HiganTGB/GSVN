package com.gsvn.paymentservice.service;


import com.gsvn.paymentservice.common.PageResponse;
import com.gsvn.paymentservice.model.dto.request.PaymentRequest;
import com.gsvn.paymentservice.model.entity.PaymentStatus;
import com.gsvn.paymentservice.model.entity.PaymentTransaction;
import com.gsvn.paymentservice.queue.message.PaymentRequestMessage;


public interface PaymentService {


    PaymentTransaction saveTransaction(PaymentRequestMessage msg, PaymentStatus status);

    void confirmManualPayment(Long transactionId, String adminNote);


    PageResponse<PaymentTransaction> getTransactions(String keyword, String status, String provider,
                                                            String sortField, String sortOrder,
                                                            int page, int size);
    void confirmCodPayment(PaymentRequest request);
}