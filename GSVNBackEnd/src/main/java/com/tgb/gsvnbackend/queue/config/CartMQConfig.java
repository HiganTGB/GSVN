package com.tgb.gsvnbackend.queue.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CartMQConfig {

    // Result order init
    @Bean
    public Queue cartResultConsumerQueue() {
        return QueueBuilder.durable("cart-result-queue").build();
    }

    @Bean
    public Binding cartConsumerBinding(Queue cartResultConsumerQueue,@Qualifier("sagaExchange") Exchange exchange) {
        return BindingBuilder.bind(cartResultConsumerQueue).to(exchange).with("cart.result").noargs();
    }
}
