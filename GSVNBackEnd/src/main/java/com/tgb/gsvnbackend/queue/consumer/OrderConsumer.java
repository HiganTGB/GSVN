package com.tgb.gsvnbackend.queue.consumer;


import com.tgb.gsvnbackend.model.entity.Order;
import com.tgb.gsvnbackend.model.enumeration.PaymentStatus;
import com.tgb.gsvnbackend.model.enumeration.State;
import com.tgb.gsvnbackend.queue.message.OrderInitMessage;
import com.tgb.gsvnbackend.queue.message.OrderPaidMessage;
import com.tgb.gsvnbackend.queue.message.OrderPaymentMessage;
import com.tgb.gsvnbackend.queue.message.OrderReversedMessage;
import com.tgb.gsvnbackend.service.impl.OrderServiceImp;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderConsumer {
    private final OrderServiceImp orderService;
    @Autowired
    public OrderConsumer(OrderServiceImp orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = "order-init-queue" ,concurrency = "1")
    public void receiverOrderInit(OrderInitMessage message)
    {
            List<Order.LineItem> lineItems= message.cartItems().stream().map(x->new Order.LineItem("0",x.skuId(),"noname",x.quantity(),new Order.Pricing(BigDecimal.ZERO,BigDecimal.ZERO))).toList();
            Order order= Order.builder()
                    .cartId(message.cardId())
                    .note(message.note())
                    .lineItems(lineItems)
                    .payment(new Order.Payment(message.paymentMethod(),PaymentStatus.Pending.name(),"none"))
                    .shippingAddress(new Order.ShippingAddress(message.receiver(),message.street(),message.city(),message.state(),message.zip(),message.phone()))
                    .state(State.Pending)
                    .ipAddress(message.ipAddress())
                    .build();
            orderService.create(order);
    }
    @RabbitListener(queues = "order-reserved-queue" ,concurrency = "1")
    public void receiverOrderReserved(OrderReversedMessage message)
    {
        String orderId=message.orderId();
        if(message.status())
        {
            List<Order.LineItem> lineItems=message.lineItems().stream().map(x->new Order.LineItem(x.id(),x.sku(),x.name(),x.quantity(),new Order.Pricing(x.retail(),x.sale()))).toList();
            orderService.orderReversedSuccess(orderId,lineItems);
        }else
        {
            orderService.orderReversedFail(orderId);
        }
    }
    @RabbitListener(queues = "order-payment-created-queue" ,concurrency = "1")
    public void receiverOrderPayment(OrderPaymentMessage message)
    {
        if(message.status())
        {
           orderService.orderPayment(message.orderId(),message.paymentId(), message.urlPayment());
        }
        orderService.orderPayment(message.orderId(),message.paymentId(), "error");

    }
    @RabbitListener(queues = "order-paid-queue" ,concurrency = "1")
    public void receiverOrderPaid(OrderPaidMessage message)
    {
            orderService.orderPaid(message.orderId(),message.paymentId(),message.success());
    }
} ;
