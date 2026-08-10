package com.gsvn.promotionservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Thống nhất dùng DirectExchange như OrderService
    public static final String VOUCHER_EXCHANGE = "voucher.exchange";

    // Queues - Khớp hoàn toàn tên với OrderService
    public static final String VOUCHER_APPLY_REQ_QUEUE = "voucher.apply.request.queue";
    public static final String VOUCHER_APPLY_RES_QUEUE = "voucher.apply.response.queue";

    // Routing Keys - Khớp hoàn toàn với OrderService
    public static final String VOUCHER_APPLY_REQ_KEY = "voucher.apply.request";
    public static final String VOUCHER_APPLY_RES_KEY = "voucher.apply.response";
    public static final String VOUCHER_COMPENSATE_REQ_KEY = "voucher.compensate.request";
    public static final String VOUCHER_COMPENSATE_REQ_QUEUE = "voucher.compensate.request.queue";
    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public DirectExchange voucherExchange() {
        return new DirectExchange(VOUCHER_EXCHANGE);
    }

    @Bean
    public Queue voucherApplyRequestQueue() {
        return new Queue(VOUCHER_APPLY_REQ_QUEUE, true);
    }

    @Bean
    public Binding voucherApplyRequestBinding() {
        return BindingBuilder.bind(voucherApplyRequestQueue())
                .to(voucherExchange())
                .with(VOUCHER_APPLY_REQ_KEY);
    }

    @Bean
    public Binding voucherCompensateBinding() {
        return BindingBuilder.bind(voucherApplyRequestQueue())
                .to(voucherExchange())
                .with(VOUCHER_COMPENSATE_REQ_KEY);
    }

    @Bean
    public Queue voucherApplyResponseQueue() {
        return new Queue(VOUCHER_APPLY_RES_QUEUE, true);
    }
    @Bean

    public Queue voucherCompensateRequestQueue() {

        return QueueBuilder.durable(VOUCHER_COMPENSATE_REQ_QUEUE).build();

    }
    @Bean
    public Binding voucherApplyResponseBinding() {
        return BindingBuilder.bind(voucherApplyResponseQueue())
                .to(voucherExchange())
                .with(VOUCHER_APPLY_RES_KEY);
    }
}