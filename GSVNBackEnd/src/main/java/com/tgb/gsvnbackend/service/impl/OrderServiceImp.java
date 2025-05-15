package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.model.entity.Order;
import com.tgb.gsvnbackend.model.enumeration.PaymentMethod;
import com.tgb.gsvnbackend.model.enumeration.PaymentStatus;
import com.tgb.gsvnbackend.model.enumeration.State;
import com.tgb.gsvnbackend.queue.producer.OrderProducer;
import com.tgb.gsvnbackend.repository.mongoRepository.OrderRepository;
import com.tgb.gsvnbackend.service.CachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImp {
    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;
    private final CachingService cachingService;
    private final String CACHE_ORDER_LIFETIME_PREFIX="orderTime:";
    @Autowired
    public OrderServiceImp(OrderRepository orderRepository, OrderProducer orderProducer, CachingService cachingService) {
        this.orderRepository = orderRepository;
        this.orderProducer = orderProducer;
        this.cachingService = cachingService;
    }
    public void create(Order order)
    {
        Order savedOrder= orderRepository.save(order);
        orderProducer.sendItemReverse(order);
    }
    public void orderReversedSuccess(String orderId, List<Order.LineItem> lineItemList)
    {
        orderRepository.findById(orderId).ifPresent(order -> {
            String cartId=order.getCartId();
            order.setLineItems(lineItemList);
            BigDecimal parallelSubTotal = lineItemList.parallelStream()
                    .map(item -> item.getPricing().getSale().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setState((Objects.equals(
                    order.getPayment().getMethod(), PaymentMethod.COD.name()))?
                    State.Processing:State.Pending );
            Order savedOrder= orderRepository.save(order);
            // set redis order expired time payment
            if(Objects.equals(savedOrder.getPayment().getMethod(), PaymentMethod.VNPAY.name())) {
                cachingService.putWithExpiration(CACHE_ORDER_LIFETIME_PREFIX, orderId, 24, TimeUnit.HOURS);
            }
            orderProducer.sentPaymentInit(savedOrder);
        });
    }
    public void orderReversedFail(String orderId)
    {
        orderRepository.findById(orderId).ifPresent(order -> {
            String cartId=order.getCartId();
            order.setState(State.Reject);
            orderRepository.save(order);
            orderProducer.sentCartFailResult(order);
        });
    }

    public void orderPayment(String orderId,String paymentId,String urlPayment)
    {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setPayment(new Order.Payment(
                    order.getPayment().getMethod(),
                    paymentId,
                    PaymentStatus.Pending.name()));
            orderRepository.save(order);
            orderProducer.sentCartSuccessResult(order,urlPayment);
        });
    }
    public void orderPaid(String orderId, String paymentId, boolean success) {
        orderRepository.findById(orderId).ifPresent(order -> {
            if (order.getPayment() != null && order.getPayment().getPaymentId().equals(paymentId)) {
                String paymentStatus = success ? PaymentStatus.Success.name() : PaymentStatus.Failed.name();
                order.setPayment(new Order.Payment(
                        order.getPayment().getMethod(),
                        paymentId,
                        paymentStatus));
                if (success) {
                    order.setState(order.getState() == State.Delivered ? State.Success : State.Processing);
                    cachingService.deleteById(CACHE_ORDER_LIFETIME_PREFIX, orderId);
                }
                orderRepository.save(order);
            }
        });
    }


}
