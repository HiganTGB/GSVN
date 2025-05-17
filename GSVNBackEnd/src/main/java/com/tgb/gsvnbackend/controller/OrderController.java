package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.lib.Oath2UtilsConverter;
import com.tgb.gsvnbackend.model.entity.Order;
import com.tgb.gsvnbackend.model.enumeration.State;
import com.tgb.gsvnbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<Page<Order>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) State state) {
        Page<Order> orders = orderService.getAllOrders(page, size, sortBy, sortDirection, userId, keyword, state);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        Order order = orderService.getOrderById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("/{id}/state")
    @PreAuthorize("hasAnyAuthority('ROLE_staff')or hasAnyAuthority('ROLE_admin')")
    public ResponseEntity<Order> changeState(@PathVariable String id, @RequestParam State state) {
        Order updatedOrder = orderService.changeState(id, state);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @PostMapping("/{id}/payment/retry")
    @PreAuthorize("hasAnyAuthority('ROLE_customer')")
    public ResponseEntity<String> createOrderPaymentAgain(@PathVariable String id, Principal user) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body( orderService.createOrderPaymentAgain(id,user));
    }

}

