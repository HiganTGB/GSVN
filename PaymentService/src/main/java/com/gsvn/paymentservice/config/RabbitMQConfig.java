package com.gsvn.paymentservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_EXCHANGE = "payment.exchange";


    public static final String PAYMENT_REQ_QUEUE = "payment.request.queue";


    public static final String PAYMENT_REQ_ROUTING_KEY = "payment.request.key";

    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String PAYMENT_RES_ROUTING_KEY = "payment.response.key";

    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed.res.key";

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue paymentReqQueue() {
        return QueueBuilder.durable(PAYMENT_REQ_QUEUE).build();
    }

    @Bean
    public Binding bindingPaymentReq() {
        return BindingBuilder.bind(paymentReqQueue())
                .to(paymentExchange())
                .with(PAYMENT_REQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}