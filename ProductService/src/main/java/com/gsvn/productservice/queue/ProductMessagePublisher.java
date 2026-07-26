package com.gsvn.productservice.queue;

import com.gsvn.productservice.config.RabbitMQConfig;
import com.gsvn.productservice.queue.message.SkuValidateResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    //(10)
    public void sendSkuValidateResponse(SkuValidateResponseMessage response) {
        log.info("Publishing SKU validation response to RabbitMQ for Order: {}", response.getOrderCode());

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PRODUCT_EXCHANGE,
                    RabbitMQConfig.SKU_VALIDATE_RES_KEY,
                    response
            );
        } catch (Exception e) {
            log.error("Error while publishing message to RabbitMQ", e);
            throw e;
        }
    }
}