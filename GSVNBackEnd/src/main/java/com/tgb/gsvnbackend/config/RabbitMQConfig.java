package com.tgb.gsvnbackend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMQConfig {
    @Value("${saga.direct-exchange-name}")
    public String directExchange;

    @Bean
    public Exchange sagaExchange() {
        return ExchangeBuilder.directExchange(directExchange).build();
    }
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}

