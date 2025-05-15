package com.tgb.gsvnbackend.queue.producer;

import com.tgb.gsvnbackend.config.RabbitMQConfig;
import com.tgb.gsvnbackend.model.domain.LineItemDomain;
import com.tgb.gsvnbackend.queue.message.OrderReversedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class InventoryProducer {
    private final RabbitTemplate rabbitTemplate;
    @Value("${saga.direct-exchange-name}")
    private String directExchange;
    @Autowired
    public InventoryProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    public void sendItemReversedSuccess(String orderId, List<LineItemDomain> lineItems)
    {
        rabbitTemplate.convertAndSend(directExchange,"order.reversed",new OrderReversedMessage(orderId,true,lineItems));
    }
    public void sendItemReversedFail(String orderId)
    {
        rabbitTemplate.convertAndSend(directExchange,"order.reversed",new OrderReversedMessage(orderId,false,null));
    }
}
