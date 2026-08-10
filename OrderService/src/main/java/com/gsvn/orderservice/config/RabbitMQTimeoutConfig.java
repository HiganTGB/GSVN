package com.gsvn.orderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTimeoutConfig {
    @Value("${app.order-time-out}")
    private Integer timeout;
    public static final String ORDER_EXCHANGE = "order.exchange";


    public static final String ORDER_HOLD_QUEUE = "order.hold.queue";
    public static final String ORDER_HOLD_KEY = "order.hold.key";


    public static final String ORDER_CANCEL_QUEUE = "order.cancel.queue";
    public static final String ORDER_CANCEL_EXCHANGE = "order.cancel.exchange";
    public static final String ORDER_CANCEL_KEY = "order.cancel.key";

    @Bean
    public DirectExchange orderCancelExchange() {
        return new DirectExchange(ORDER_CANCEL_EXCHANGE);
    }

    @Bean
    public Queue holdQueue() {
        return QueueBuilder.durable(ORDER_HOLD_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_CANCEL_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_CANCEL_KEY)
                .withArgument("x-message-ttl", timeout * 60 * 1000)
                .build();
    }

    @Bean
    public Binding holdBinding(DirectExchange orderExchange) {
        return BindingBuilder.bind(holdQueue()).to(orderExchange).with(ORDER_HOLD_KEY);
    }

    @Bean
    public Queue cancelQueue() {
        return new Queue(ORDER_CANCEL_QUEUE, true);
    }

    @Bean
    public Binding cancelBinding() {
        return BindingBuilder.bind(cancelQueue()).to(orderCancelExchange()).with(ORDER_CANCEL_KEY);
    }
}