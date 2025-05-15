package com.tgb.gsvnbackend.queue.producer;

import com.tgb.gsvnbackend.config.RabbitMQConfig;
import com.tgb.gsvnbackend.model.domain.LineItemDomain;
import com.tgb.gsvnbackend.model.entity.Order;
import com.tgb.gsvnbackend.model.enumeration.PaymentMethod;
import com.tgb.gsvnbackend.queue.message.CartResultMessage;
import com.tgb.gsvnbackend.queue.message.ItemReverseMessage;
import com.tgb.gsvnbackend.queue.message.PaymentInitMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderProducer {
    @Value("${saga.direct-exchange-name}")
    private String directExchange;
    private final RabbitTemplate rabbitTemplate;
    private String EMPTY_PREVALUE="empty";
    @Autowired
    public OrderProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendItemReverse(Order order)
    {
        rabbitTemplate.convertAndSend(directExchange,"item.reverser",new ItemReverseMessage(order.getId(),order.getLineItems().stream().map(x->new LineItemDomain(x.getSku(),EMPTY_PREVALUE,EMPTY_PREVALUE,x.getQuantity(), BigDecimal.ZERO,BigDecimal.ZERO)).toList()));
    }
    public void sentPaymentInit(Order order)
    {
            rabbitTemplate.convertAndSend(directExchange,"payment.init",new PaymentInitMessage(order.getId(),order.getUserId(),order.getSubTotal(),order.getPayment().getMethod(),order.getIpAddress()));

    }
    public void sentCartSuccessResult(Order order,String urlPayment)
    {
            rabbitTemplate.convertAndSend(directExchange,"cart.result",new CartResultMessage(order.getCartId(),order.getId(),true,urlPayment));
    }
    public void sentCartFailResult(Order order)
    {
        rabbitTemplate.convertAndSend(directExchange,"cart.result",new CartResultMessage(order.getCartId(),order.getId(),false,"none"));
    }
}
