package com.tgb.gsvnbackend.queue.producer;

import com.tgb.gsvnbackend.config.RabbitMQConfig;
import com.tgb.gsvnbackend.model.domain.CartItemDomain;
import com.tgb.gsvnbackend.model.entity.Cart;
import com.tgb.gsvnbackend.model.enumeration.PaymentMethod;
import com.tgb.gsvnbackend.queue.message.OrderInitMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartProducer {
    @Value("${saga.direct-exchange-name}")
    private String directExchange;
    private final RabbitTemplate rabbitTemplate;
    @Autowired
    public CartProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderInit(Cart cart,
                              String note,
                              String receiver,
                              String street,
                              String city,
                              String state,
                              String zip,
                              String phone,
                              PaymentMethod paymentMethod,
                              String ipAddress)
    {
        List<CartItemDomain> cartItems=cart.getCartItems().stream().map(x->new CartItemDomain(x.getSkuId(),x.getQuantity())).toList();
        rabbitTemplate.convertAndSend(directExchange,
                "order.init",
                new OrderInitMessage(
                cart.getCardId(),
                cart.getUserId(),
                note,
                cartItems,
                receiver,street,city,state,zip,phone,
                paymentMethod.name(),
                ipAddress)
                );
    }
}
