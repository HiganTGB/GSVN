package com.tgb.gsvnbackend.queue.consumer;

import com.tgb.gsvnbackend.model.domain.PaymentDomain;
import com.tgb.gsvnbackend.queue.message.PaymentInitMessage;
import com.tgb.gsvnbackend.service.impl.PaymentServiceImp;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {
    private final PaymentServiceImp paymentService;
    @Autowired
    public PaymentConsumer(PaymentServiceImp paymentService) {
        this.paymentService = paymentService;
    }
    @RabbitListener(queues = "payment-init-queue" ,concurrency = "1")
    public void receiverPaymentInit(PaymentInitMessage message)
    {
        paymentService.createPayment(new PaymentDomain(message.orderId(),message.userId(),message.amount(),message.paymentMethod(), message.ipAddress()));
    }

}
