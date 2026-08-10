package com.gsvn.orderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";

    public static final String PRODUCT_EXCHANGE = "product.exchange";
    //(4)
    public static final String SKU_VALIDATE_REQ_QUEUE = "sku.validate.request.queue";
    public static final String SKU_VALIDATE_REQ_KEY = "sku.validate.request";
    //(11)
    public static final String SKU_VALIDATE_RES_QUEUE = "sku.validate.response.queue";
    public static final String SKU_VALIDATE_RES_KEY = "sku.validate.response";


    public static final String INVENTORY_EXCHANGE = "inventory.exchange";

    public static final String INV_RESERVE_REQ_QUEUE = "inventory.reserve.request.queue";
    //(15)
    public static final String INV_RESERVE_REQ_KEY = "inventory.reserve.request";
    //(21)
    public static final String INV_RESERVE_RES_QUEUE = "inventory.reserve.response.queue";
    public static final String INV_RESERVE_RES_KEY = "inventory.reserve.response";


    public static final String VOUCHER_EXCHANGE = "voucher.exchange";
    public static final String VOUCHER_APPLY_REQ_QUEUE = "voucher.apply.request.queue";
    public static final String VOUCHER_APPLY_REQ_KEY = "voucher.apply.request";
    public static final String VOUCHER_APPLY_RES_QUEUE = "voucher.apply.response.queue";
    public static final String VOUCHER_APPLY_RES_KEY = "voucher.apply.response";


    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    public static final String PAYMENT_REQ_QUEUE = "payment.request.queue";
    public static final String PAYMENT_REQ_KEY = "payment.request.key";

    public static final String PAYMENT_RES_QUEUE = "payment.response.queue";
    public static final String PAYMENT_RES_KEY = "payment.response.key";

    public static final String PAYMENT_COMPLETED_RES_QUEUE = "payment.completed.res.queue";
    public static final String PAYMENT_COMPLETED_RES_KEY = "payment.completed.res.key";


    public static final String INV_COMPENSATE_REQ_KEY = "inventory.compensate.request";
    public static final String VOUCHER_COMPENSATE_REQ_KEY = "voucher.compensate.request";



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
    public DirectExchange orderExchange() { return new DirectExchange(ORDER_EXCHANGE); }

    // =========================================================================
    // SECTION 1: PRODUCT VALIDATION
    // =========================================================================
    @Bean
    public DirectExchange productExchange() { return new DirectExchange(PRODUCT_EXCHANGE); }

    @Bean
    public Queue skuRequestQueue() { return new Queue(SKU_VALIDATE_REQ_QUEUE, true); }

    @Bean
    public Binding skuRequestBinding() {
        return BindingBuilder.bind(skuRequestQueue()).to(orderExchange()).with(SKU_VALIDATE_REQ_KEY);
    }

    @Bean
    public Queue skuResponseQueue() { return new Queue(SKU_VALIDATE_RES_QUEUE, true); }

    @Bean
    public Binding skuResponseBinding() {
        return BindingBuilder.bind(skuResponseQueue()).to(productExchange()).with(SKU_VALIDATE_RES_KEY);
    }

    // =========================================================================
    // SECTION 2: INVENTORY RESERVATION & COMPENSATION
    // =========================================================================
    @Bean
    public DirectExchange inventoryExchange() { return new DirectExchange(INVENTORY_EXCHANGE); }

    @Bean
    public Queue invReserveRequestQueue() { return new Queue(INV_RESERVE_REQ_QUEUE, true); }

    @Bean
    public Binding invReserveRequestBinding() {
        return BindingBuilder.bind(invReserveRequestQueue()).to(inventoryExchange()).with(INV_RESERVE_REQ_KEY);
    }

    @Bean
    public Queue invReserveResponseQueue() { return new Queue(INV_RESERVE_RES_QUEUE, true); }

    @Bean
    public Binding invReserveResponseBinding() {
        return BindingBuilder.bind(invReserveResponseQueue()).to(inventoryExchange()).with(INV_RESERVE_RES_KEY);
    }

    // Luồng bù đắp (Compensate) cho Inventory
    @Bean
    public Binding invCompensateBinding() {
        return BindingBuilder.bind(invReserveRequestQueue())
                .to(inventoryExchange())
                .with(INV_COMPENSATE_REQ_KEY);
    }

    // =========================================================================
    // SECTION 3: VOUCHER APPLICATION & COMPENSATION
    // =========================================================================
    @Bean
    public DirectExchange voucherExchange() { return new DirectExchange(VOUCHER_EXCHANGE); }

    @Bean
    public Queue voucherApplyRequestQueue() { return new Queue(VOUCHER_APPLY_REQ_QUEUE, true); }

    @Bean
    public Binding voucherApplyRequestBinding() {
        return BindingBuilder.bind(voucherApplyRequestQueue()).to(voucherExchange()).with(VOUCHER_APPLY_REQ_KEY);
    }

    @Bean
    public Queue voucherApplyResponseQueue() { return new Queue(VOUCHER_APPLY_RES_QUEUE, true); }

    @Bean
    public Binding voucherApplyResponseBinding() {
        return BindingBuilder.bind(voucherApplyResponseQueue()).to(voucherExchange()).with(VOUCHER_APPLY_RES_KEY);
    }

    @Bean
    public Binding voucherCompensateBinding() {
        return BindingBuilder.bind(voucherApplyRequestQueue())
                .to(voucherExchange())
                .with(VOUCHER_COMPENSATE_REQ_KEY);
    }
    @Bean
    public DirectExchange paymentExchange() { return new DirectExchange(PAYMENT_EXCHANGE); }

    @Bean
    public Queue paymentReqQueue() { return new Queue(PAYMENT_REQ_QUEUE, true); }

    @Bean
    public Binding paymentReqBinding() {
        return BindingBuilder.bind(paymentReqQueue()).to(paymentExchange()).with(PAYMENT_REQ_KEY);
    }

    @Bean
    public Queue paymentResQueue() { return new Queue(PAYMENT_RES_QUEUE, true); }

    @Bean
    public Binding paymentResBinding() {
        return BindingBuilder.bind(paymentResQueue()).to(orderExchange()).with(PAYMENT_RES_KEY);
    }

    @Bean
    public Queue paymentCompletedResQueue() { return new Queue(PAYMENT_COMPLETED_RES_QUEUE, true); }

    @Bean
    public Binding paymentCompletedResBinding() {
        return BindingBuilder.bind(paymentCompletedResQueue()).to(orderExchange()).with(PAYMENT_COMPLETED_RES_KEY);
    }

}