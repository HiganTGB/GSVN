package com.gsvn.promotionservice.queue;

import com.gsvn.promotionservice.config.RabbitMQConfig;
import com.gsvn.promotionservice.queue.message.VoucherResponseMessage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoucherMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    @CircuitBreaker(name = "rabbitmq-publisher")
    public void sendVoucherResponse(VoucherResponseMessage message) {
        log.info("Publishing Voucher Response for Order: {} - Success: {}",
                message.getOrderCode(), message.isSuccess());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.VOUCHER_EXCHANGE,
                RabbitMQConfig.VOUCHER_APPLY_RES_KEY,
                message
        );
    }
}