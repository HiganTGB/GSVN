package com.tgb.gsvnbackend.service.impl;


import com.tgb.gsvnbackend.config.VNPAYConfig;
import com.tgb.gsvnbackend.lib.VNPayUtils;
import com.tgb.gsvnbackend.model.domain.PaymentDomain;
import com.tgb.gsvnbackend.model.entity.Payment;
import com.tgb.gsvnbackend.model.enumeration.PaymentMethod;
import com.tgb.gsvnbackend.model.enumeration.PaymentStatus;
import com.tgb.gsvnbackend.queue.producer.PaymentProducer;
import com.tgb.gsvnbackend.repository.jpaRepository.PaymentRepository;
import com.tgb.gsvnbackend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import static com.tgb.gsvnbackend.lib.Oath2UtilsConverter.getUserId;
@Service

public class PaymentServiceImp implements PaymentService {
    private final VNPAYConfig vnPayConfig;
    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    @Autowired
    public PaymentServiceImp(VNPAYConfig vnPayConfig, PaymentRepository paymentRepository, PaymentProducer paymentProducer) {
        this.vnPayConfig = vnPayConfig;
        this.paymentRepository = paymentRepository;
        this.paymentProducer = paymentProducer;
    }
    private Payment create(Payment payment)
    {
        return paymentRepository.save(payment);
    }
    public void createPayment(PaymentDomain request)
    {
        if(request.paymentMethod().equals(PaymentMethod.VNPAY.name()))
        {
            createVnPayPayment(request);
            return;
        }
        if(request.paymentMethod().equals(PaymentMethod.COD.name()))
        {
            createCODPayment(request);
            return;
        }
        paymentProducer.sendOrderPayment(request.orderId(),null,null,false);

    }
    private void createVnPayPayment(PaymentDomain request) {
        Payment payment=Payment.builder()
                .paymentMethod(request.paymentMethod())
                .paymentStatus(PaymentStatus.Pending.name())
                .amount(request.amount())
                .userId(request.userId())
                .currency("VNĐ")
                .build();
        Payment savedPayment=create(payment);
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(String.valueOf(savedPayment.getPaymentId()),savedPayment.getAmount().doubleValue());
        vnpParamsMap.put("vnp_IpAddr", request.ipAddress());
        //build query url
        String queryUrl = VNPayUtils.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtils.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtils.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentURL= vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        paymentProducer.sendOrderPayment(savedPayment.getOrderId(), String.valueOf(savedPayment.getPaymentId()),paymentURL,true);
    }
    private void createCODPayment(PaymentDomain request)
    {
        Payment payment=Payment.builder()
                .paymentMethod(request.paymentMethod())
                .paymentStatus(PaymentStatus.Pending.name())
                .amount(request.amount())
                .userId(request.userId())
                .transactionId("Shipper")
                .currency("VNĐ")
                .build();
        Payment savedPayment=create(payment);
        paymentProducer.sendOrderPayment(savedPayment.getOrderId(), String.valueOf(savedPayment.getPaymentId()),null,true);
    }

    public void paidVNPAYResult(int paymentId,String TransactionNo,boolean success)
    {
            paymentRepository.findById(paymentId).ifPresent(
                    x->{
                        x.setPaidDate(LocalDateTime.now());
                        x.setTransactionId(TransactionNo);
                        x.setPaymentStatus((success)? PaymentStatus.Success.name() : PaymentStatus.Failed.name());
                        paymentRepository.save(x);
                        paymentProducer.sendOrderPaid(x);
                    }
            );
    }
    public void paidCOD(int paymentId, Principal user)
    {
        String userId=getUserId(user);
        paymentRepository.findById(paymentId).ifPresent(
                x->{
                    if(x.getPaymentMethod().equals(PaymentMethod.COD.name())) {
                        x.setPaidDate(LocalDateTime.now());
                        x.setPaymentStatus(PaymentStatus.Success.name());
                        x.setTransactionId(userId);
                        paymentRepository.save(x);
                        paymentProducer.sendOrderPaid(x);
                    }
                }
        );
    }
}