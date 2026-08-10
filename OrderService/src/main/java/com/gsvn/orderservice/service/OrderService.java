package com.gsvn.orderservice.service;


import com.gsvn.orderservice.common.PageResponse;
import com.gsvn.orderservice.model.dto.request.OrderCreateRequest;
import com.gsvn.orderservice.model.dto.response.OrderResponse;
import jakarta.servlet.http.HttpServletRequest;
public interface OrderService {
    String createOrder(OrderCreateRequest request, HttpServletRequest httpServletRequest) ;
    String createStaffOrder(OrderCreateRequest request) ;
    PageResponse<OrderResponse> searchOrders(
            String warehouseCode, String code, String email, String phone, String status,
            int page, int size, String sortBy, String direct) ;
    OrderResponse getOrderDetail(Long orderId) ;
    OrderResponse approveOrder(Long orderId, String note);
    PageResponse<OrderResponse> getCustomerOrders(
            String orderCode, String status, int page, int size) ;
    void updateFromShipment(String orderCode,String value);



}