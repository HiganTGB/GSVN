package com.tgb.gsvnbackend.service;

import com.tgb.gsvnbackend.model.domain.PaymentDomain;

import java.security.Principal;

public interface PaymentService {
    void createPayment(PaymentDomain request);
    void paidVNPAYResult(int paymentId, String TransactionNo,boolean success);
    void paidCOD(int paymentId, Principal user);
}
