package com.gsvn.paymentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsvn.paymentservice.config.VNPayConfig;
import com.gsvn.paymentservice.config.VNPayProperties;
import com.gsvn.paymentservice.mapper.MessageLogMapper;
import com.gsvn.paymentservice.mapper.PaymentTransactionMapper;
import com.gsvn.paymentservice.queue.message.PaymentRequestMessage;
import com.gsvn.paymentservice.service.VNPayService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class VNPayServiceTest {

    @Mock
    private VNPayConfig vnpayConfig;
    @Mock
    private VNPayProperties vnpayProperties;
    @Mock
    private PaymentTransactionMapper paymentMapper;
    @Mock
    private MessageLogMapper messageLogMapper;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private VNPayService vnpayService;

    @Test
    void testCreatePaymentUrl_Success() {

        PaymentRequestMessage message = PaymentRequestMessage.builder()
                .orderCode("ORD1001")
                .amount(new BigDecimal("100000"))
                .clientIpAddress("127.0.0.1")
                .expireMinutes(15)
                .build();


        lenient().when(vnpayProperties.getVersion()).thenReturn("2.1.0");
        lenient().when(vnpayProperties.getCommand()).thenReturn("pay");
        lenient().when(vnpayProperties.getTmnCode()).thenReturn("DEMO_TMN");
        lenient().when(vnpayProperties.getHashSecret()).thenReturn("SECRET_KEY");
        lenient().when(vnpayProperties.getPayUrl()).thenReturn("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        lenient().when(vnpayProperties.getReturnUrl()).thenReturn("http://localhost:8080/callback");
        lenient().when(vnpayProperties.getCurrCode()).thenReturn("VND");
        lenient().when(vnpayProperties.getOrderType()).thenReturn("other");


        when(vnpayConfig.hmacSHA512(anyString(), anyString())).thenReturn("FAKE_HASH_12345");


        String resultUrl = vnpayService.createPaymentUrl(message).getCheckoutUrl();


        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("vnp_Amount=10000000"));
        assertTrue(resultUrl.contains("vnp_TxnRef=PAYORD1001"));
        assertTrue(resultUrl.contains("vnp_SecureHash=FAKE_HASH_12345"));


        verify(paymentMapper, times(1)).insert(any());

        System.out.println("Generated URL: " + resultUrl);
    }
}