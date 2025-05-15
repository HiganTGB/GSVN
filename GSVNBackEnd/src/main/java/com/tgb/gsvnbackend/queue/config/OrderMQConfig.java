package com.tgb.gsvnbackend.queue.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderMQConfig {


    // Order Init Event
    @Bean
    public Queue orderInitConsumerQueue() {
        return QueueBuilder.durable("order-init-queue").build();
    }

    @Bean
    public Binding orderInitConsumerBinding(Queue orderInitConsumerQueue,@Qualifier("sagaExchange") Exchange exchange) {
        return BindingBuilder.bind(orderInitConsumerQueue).to(exchange).with("order.init").noargs();
    }
    // Item Reserved Event
    @Bean
    public Queue orderReservedConsumerQueue() {
        return QueueBuilder.durable("order-reserved-queue").build();
    }

    @Bean
    public Binding orderReservedBinding(Queue orderReservedConsumerQueue,@Qualifier("sagaExchange") Exchange exchange) {
        return BindingBuilder.bind(orderReservedConsumerQueue).to(exchange).with("order.reserved").noargs();
    }
    // Payment Created Event
    @Bean
    public Queue orderPaymentCreatedConsumerQueue() {
        return QueueBuilder.durable("order-payment-created-queue").build();
    }

    @Bean
    public Binding orderPaymentBinding(Queue orderPaymentCreatedConsumerQueue, Exchange exchange) {
        return BindingBuilder.bind(orderPaymentCreatedConsumerQueue).to(exchange).with("order.payment.created").noargs();
    }
    // Order Paid Event
    @Bean
    public Queue orderPaidConsumerQueue() {
        return QueueBuilder.durable("order-paid-queue").build();
    }

    @Bean
    public Binding orderPaidBinding(Queue orderPaidConsumerQueue, Exchange exchange) {
        return BindingBuilder.bind(orderPaidConsumerQueue).to(exchange).with("order.paid").noargs();
    }
    // Order Expired Event
    @Bean
    public Queue orderExpiredConsumerQueue() {
        return QueueBuilder.durable("order-expired-queue").build();
    }

    @Bean
    public Binding  orderExpiredBinding(Queue  orderExpiredConsumerQueue, Exchange exchange) {
        return BindingBuilder.bind( orderExpiredConsumerQueue).to(exchange).with("order.expired").noargs();
    }
    // Order Cancel Event
    @Bean
    public Queue orderCancelConsumerQueue() {
        return QueueBuilder.durable("order-cancel-queue").build();
    }

    @Bean
    public Binding  orderCancelBinding(Queue  orderCancelConsumerQueue, Exchange exchange) {
        return BindingBuilder.bind( orderCancelConsumerQueue).to(exchange).with("order.cancel").noargs();
    }

}
