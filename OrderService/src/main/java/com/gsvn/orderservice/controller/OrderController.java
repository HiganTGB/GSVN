package com.gsvn.orderservice.controller;

import com.gsvn.orderservice.common.ApiResponse;
import com.gsvn.orderservice.common.PageResponse;
import com.gsvn.orderservice.exc.AppException;
import com.gsvn.orderservice.exc.ErrorCode;
import com.gsvn.orderservice.model.dto.request.OrderApproveRequest;
import com.gsvn.orderservice.model.dto.request.OrderCreateRequest;
import com.gsvn.orderservice.model.dto.response.OrderResponse;
import com.gsvn.orderservice.model.enums.OrderStatus;
import com.gsvn.orderservice.service.AuthenticationService;
import com.gsvn.orderservice.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;
    AuthenticationService authService;

    @PostMapping("/checkout")
    public ApiResponse<String> customerCreateOrder(@RequestBody @Valid OrderCreateRequest request, HttpServletRequest httpServletRequest) {
        log.error(request.toString());
        Long customerIdFromToken = authService.getCustomerIdFromToken();
        request.setCustomerId(customerIdFromToken);
        String orderCode = orderService.createOrder(request,httpServletRequest);

        return new ApiResponse<>(orderCode);
    }
    @PostMapping("/pos-checkout")
    public ApiResponse<String> staffCreateOrder(@RequestBody @Valid OrderCreateRequest request) {
        Long staffId = authService.getStaffIdFromToken();

        if (staffId == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        String orderCode = orderService.createStaffOrder(request);

        return new ApiResponse<>(orderCode);
    }
    @GetMapping("/my-order")
    public ApiResponse<PageResponse<OrderResponse>> getMyOrders(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderResponse> response = orderService.getCustomerOrders(code, status, page, size);

        return new ApiResponse<>(response);
    }
    @GetMapping("/search")
    public ApiResponse<PageResponse<OrderResponse>> searchOrders(
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direct) {

        PageResponse<OrderResponse> response = orderService.searchOrders(
                warehouseCode, code, email, phone, status, page, size, sortBy, direct
        );
        return new ApiResponse<>(response);
    }
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderDetail(@PathVariable Long id) {
        OrderResponse detail = orderService.getOrderDetail(id);
        return new ApiResponse<>(detail);
    }
    @PostMapping("/{id}/approve")
    public ApiResponse<OrderResponse> approve(
            @PathVariable Long id,
            @RequestBody(required = false) OrderApproveRequest request) {

        OrderResponse result = orderService.approveOrder(id, request.getNote());
        return new ApiResponse<>("Duyệt đơn và tách kiện hàng thành công", result);
    }
    @PutMapping("/internal/{code}/updateShipment")
    public ApiResponse<Boolean> updateStatus(@PathVariable String code, String status) {
        orderService.updateFromShipment(code,status);
        return new ApiResponse<>(true);
    }
}