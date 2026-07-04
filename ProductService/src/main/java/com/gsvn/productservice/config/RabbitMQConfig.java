package com.gsvn.productservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String PRODUCT_EXCHANGE = "product.exchange";

    public static final String SKU_VALIDATE_REQ_QUEUE = "sku.validate.request.queue";

    public static final String SKU_VALIDATE_REQ_KEY = "sku.validate.request";
    public static final String SKU_VALIDATE_RES_KEY = "sku.validate.response";


    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
    @Bean
    public DirectExchange productExchange() {
        return new DirectExchange(PRODUCT_EXCHANGE);
    }

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }


    @Bean
    public Queue skuRequestQueue() {
        return new Queue(SKU_VALIDATE_REQ_QUEUE, true);
    }

    //(5)
    @Bean
    public Binding skuRequestBinding() {
        return BindingBuilder.bind(skuRequestQueue())
                .to(orderExchange())
                .with(SKU_VALIDATE_REQ_KEY);
    }
}