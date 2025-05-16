package com.tgb.gsvnbackend.queue.producer;


import com.tgb.gsvnbackend.config.RabbitMQConfig;
import com.tgb.gsvnbackend.model.domain.LineItemDomain;
import com.tgb.gsvnbackend.model.entity.Order;
import com.tgb.gsvnbackend.model.entity.Payment;
import com.tgb.gsvnbackend.model.enumeration.PaymentStatus;
import com.tgb.gsvnbackend.queue.message.ItemReverseMessage;
import com.tgb.gsvnbackend.queue.message.OrderPaidMessage;
import com.tgb.gsvnbackend.queue.message.OrderPaymentMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
public class PaymentProducer {
    @Value("${saga.direct-exchange-name}")
    private String directExchange;
    private final RabbitTemplate rabbitTemplate;
    @Autowired
    public PaymentProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    public void sendOrderPayment(String orderId,String paymentId,String paymentUrl,boolean success )
    {
        rabbitTemplate.convertAndSend(directExchange,"order.payment",new OrderPaymentMessage(orderId,paymentId,paymentUrl,success));
    }
    public void sendOrderPaid(Payment payment)
    {
        rabbitTemplate.convertAndSend(directExchange,"order.paid",new OrderPaidMessage(payment.getOrderId(),String.valueOf(payment.getPaymentId()),payment.getPaymentStatus().equals(PaymentStatus.Success.name())));
    }
}
