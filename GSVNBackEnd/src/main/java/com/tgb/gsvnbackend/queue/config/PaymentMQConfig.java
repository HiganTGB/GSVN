package com.tgb.gsvnbackend.queue.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentMQConfig {
    // Payment Init Event
    @Bean
    public Queue paymentInitConsumerQueue() {
        return QueueBuilder.durable("payment-init-queue").build();
    }

    @Bean
    public Binding paymentInitConsumerBinding(Queue paymentInitConsumerQueue,@Qualifier("sagaExchange") Exchange exchange) {
        return BindingBuilder.bind(paymentInitConsumerQueue).to(exchange).with("payment.init").noargs();
    }
    // Payment Reserved Event
    @Bean
    public Queue paymentReservedConsumerQueue() {
        return QueueBuilder.durable("payment-reserved-queue").build();
    }

    @Bean
    public Binding paymentReservedBinding(Queue paymentReservedConsumerQueue,@Qualifier("sagaExchange") Exchange exchange) {
        return BindingBuilder.bind(paymentReservedConsumerQueue).to(exchange).with("payment.reserved").noargs();
    }
}
