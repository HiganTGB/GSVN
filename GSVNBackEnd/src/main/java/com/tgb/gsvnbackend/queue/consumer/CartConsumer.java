package com.tgb.gsvnbackend.queue.consumer;

import com.tgb.gsvnbackend.queue.message.CartResultMessage;
import com.tgb.gsvnbackend.service.CartService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartConsumer {
    private final CartService cartService;
    @Autowired
    public CartConsumer(CartService cartService) {
        this.cartService = cartService;
    }

    @RabbitListener(queues = "cart-result-queue" ,concurrency = "1")
    public void receiveCartResult(CartResultMessage message)
    {
        String cartId=message.cartId();
        cartService.handleCartResultReceiver(cartId,message.orderId(),message.urlPayment(),message.success());
    }
}
