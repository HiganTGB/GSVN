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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import static com.tgb.gsvnbackend.lib.Oath2UtilsConverter.getUserId;
@Service
@Slf4j
public class PaymentServiceImp implements PaymentService {
    private final VNPAYConfig vnPayConfig;
    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    @Autowired
    public PaymentServiceImp(VNPAYConfig vnPayConfig, PaymentRepository paymentRepository, PaymentProducer paymentProducer) {
        this.vnPayConfig = vnPayConfig;
        this.paymentRepository = paymentRepository;
        this.paymentProducer = paymentProducer;
        log.info("PaymentServiceImp initialized.");
    }
    private Payment create(Payment payment)
    {
        log.info("Creating a new payment for user ID: {}, order ID: {}, method: {}", payment.getUserId(), payment.getOrderId(), payment.getPaymentMethod());
        return paymentRepository.save(payment);
    }
    public void createPayment(PaymentDomain request)
    {
        log.info("Processing payment creation request for order ID: {}, method: {}", request.orderId(), request.paymentMethod());
        if(request.paymentMethod().equals(PaymentMethod.VNPAY.name()))
        {
            log.info("Creating VNPAY payment for order ID: {}", request.orderId());
            createVnPayPayment(request);
            return;
        }
        if(request.paymentMethod().equals(PaymentMethod.COD.name()))
        {
            log.info("Creating COD payment for order ID: {}", request.orderId());
            createCODPayment(request);
            return;
        }
        log.warn("Unsupported payment method: {}", request.paymentMethod());
        paymentProducer.sendOrderPayment(request.orderId(),null,null,false);
        log.info("Sending order payment failure message for order ID: {}", request.orderId());

    }
    private void createVnPayPayment(PaymentDomain request) {
        Payment payment=Payment.builder()
                .paymentMethod(request.paymentMethod())
                .paymentStatus(PaymentStatus.Pending.name())
                .amount(request.amount())
                .userId(request.userId())
                .currency("VNĐ")
                .orderId(request.orderId())
                .build();
        Payment savedPayment=create(payment);
        log.info("VNPAY payment created with ID: {}", savedPayment.getPaymentId());
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(String.valueOf(savedPayment.getPaymentId()),savedPayment.getAmount().doubleValue());
        vnpParamsMap.put("vnp_IpAddr", request.ipAddress());
        //build query url
        String queryUrl = VNPayUtils.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtils.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtils.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentURL= vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        log.info("Generated VNPAY payment URL: {}", paymentURL);
        paymentProducer.sendOrderPayment(savedPayment.getOrderId(), String.valueOf(savedPayment.getPaymentId()),paymentURL,true);
        log.info("Sent order payment initialization message for order ID: {}, payment ID: {}, URL: {}", savedPayment.getOrderId(), savedPayment.getPaymentId(), paymentURL);
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
                .orderId(request.orderId())
                .build();
        Payment savedPayment=create(payment);
        log.info("COD payment created with ID: {}", savedPayment.getPaymentId());
        paymentProducer.sendOrderPayment(savedPayment.getOrderId(), String.valueOf(savedPayment.getPaymentId()),null,true);
        log.info("Sent order payment initialization message for order ID: {}, payment ID: {}, COD.", savedPayment.getOrderId(), savedPayment.getPaymentId());
    }

    public void paidVNPAYResult(int paymentId,String TransactionNo,boolean success)
    {
        log.info("Processing VNPAY payment result for payment ID: {}, transaction ID: {}, success: {}", paymentId, TransactionNo, success);
        paymentRepository.findById(paymentId).ifPresent(
                x->{
                    x.setPaidDate(LocalDateTime.now());
                    x.setTransactionId(TransactionNo);
                    x.setPaymentStatus((success)? PaymentStatus.Success.name() : PaymentStatus.Failed.name());
                    paymentRepository.save(x);
                    log.info("Payment ID {} updated - status: {}, transaction ID: {}, paid date: {}", paymentId, x.getPaymentStatus(), x.getTransactionId(), x.getPaidDate());
                    paymentProducer.sendOrderPaid(x);
                    log.info("Sent order paid message for payment ID: {}", paymentId);
                }
        );
    }
    public void paidCOD(int paymentId, Principal user)
    {
        String userId=getUserId(user);
        log.info("Processing COD payment for payment ID: {} by user ID: {}", paymentId, userId);
        paymentRepository.findById(paymentId).ifPresent(
                x->{
                    if(x.getPaymentMethod().equals(PaymentMethod.COD.name())) {
                        x.setPaidDate(LocalDateTime.now());
                        x.setPaymentStatus(PaymentStatus.Success.name());
                        x.setTransactionId(userId);
                        paymentRepository.save(x);
                        log.info("COD payment ID {} updated - status: Success, transaction ID: {}, paid date: {}", paymentId, x.getTransactionId(), x.getPaidDate());
                        paymentProducer.sendOrderPaid(x);
                        log.info("Sent order paid message for payment ID: {}", paymentId);
                    } else {
                        log.warn("Attempted to process non-COD payment with paidCOD method for payment ID: {}", paymentId);
                    }
                }
        );
    }
}