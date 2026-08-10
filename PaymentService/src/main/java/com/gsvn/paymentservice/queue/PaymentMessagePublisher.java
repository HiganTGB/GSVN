package com.gsvn.paymentservice.queue;

import com.gsvn.paymentservice.config.RabbitMQConfig;
import com.gsvn.paymentservice.queue.message.PaymentCompletedMessage;
import com.gsvn.paymentservice.queue.message.PaymentResponseMessage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    @CircuitBreaker(name = "rabbitmq-publisher")
    public void sendPaymentResponse(PaymentResponseMessage message) {
        log.info("Publishing Payment Response for Order: {}", message.getOrderCode());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.PAYMENT_RES_ROUTING_KEY,
                message
        );
    }
    @CircuitBreaker(name = "rabbitmq-publisher")
    public void sendPaymentCompletedEvent(PaymentCompletedMessage message) {
        log.info("Publishing Payment Completed Event for Order: {}", message.getOrderCode());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.PAYMENT_COMPLETED_ROUTING_KEY,
                message
        );
    }
}