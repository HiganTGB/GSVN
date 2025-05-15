package com.tgb.gsvnbackend.service;

import com.tgb.gsvnbackend.model.entity.Order;
import com.tgb.gsvnbackend.model.enumeration.State;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    void create(Order order);
    void orderReversedSuccess(String orderId, List<Order.LineItem> lineItemList);
    void orderReversedFail(String orderId);
    void orderPayment(String orderId, String paymentId, String urlPayment);
    void orderPaid(String orderId, String paymentId, boolean success);
    Page<Order> getAllOrders(int page, int size, String sortBy, String sortDirection, String userId, String keyword, State state);
    Order getOrderById(String orderId);
    Order changeState(String orderId, State state);
    String createOrderPaymentAgain(String orderId, Principal user);
}
