package com.gsvn.inventoryservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String INVENTORY_EXCHANGE = "inventory.exchange";


    public static final String INV_RESERVE_REQ_ROUTING_KEY = "inventory.reserve.request";
    public static final String INV_RESERVE_RES_ROUTING_KEY = "inventory.reserve.response";
    public static final String INV_COMPENSATE_REQ_ROUTING_KEY = "inventory.compensate.request";

    //(16)
    public static final String INV_RESERVE_REQ_QUEUE = "inventory.reserve.request.queue";
    public static final String INV_RESERVE_RES_QUEUE = "inventory.reserve.response.queue";
    public static final String INV_COMPENSATE_REQ_QUEUE = "inventory.compensate.request.queue";


    @Bean
    public DirectExchange inventoryExchange() {
        return new DirectExchange(INVENTORY_EXCHANGE);
    }

    @Bean
    public Queue invReserveReqQueue() {
        return new Queue(INV_RESERVE_REQ_QUEUE, true);
    }

    @Bean
    public Binding invReserveReqBinding() {
        return BindingBuilder.bind(invReserveReqQueue())
                .to(inventoryExchange())
                .with(INV_RESERVE_REQ_ROUTING_KEY);
    }

    @Bean
    public Queue invCompensateReqQueue() {
        return QueueBuilder.durable(INV_COMPENSATE_REQ_QUEUE).build();
    }

    @Bean
    public Binding invCompensateReqBinding() {
        return BindingBuilder.bind(invCompensateReqQueue())
                .to(inventoryExchange())
                .with(INV_COMPENSATE_REQ_ROUTING_KEY);
    }

    @Bean
    public Queue invReserveResQueue() {
        return QueueBuilder.durable(INV_RESERVE_RES_QUEUE).build();
    }

    @Bean
    public Binding invReserveResBinding() {
        return BindingBuilder.bind(invReserveResQueue())
                .to(inventoryExchange())
                .with(INV_RESERVE_RES_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}