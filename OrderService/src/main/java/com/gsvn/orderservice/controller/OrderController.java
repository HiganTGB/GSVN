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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Order Management", description = "Endpoints for online customer checkout, POS staff order creation, order tracking, approval & shipment status updates")
public class OrderController {

    OrderService orderService;
    AuthenticationService authService;

    @Operation(summary = "Customer online checkout", description = "Self-service endpoint for customers to place online orders with automatic customer identification from authentication token.")
    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ApiResponse<String> customerCreateOrder(@RequestBody @Valid OrderCreateRequest request, HttpServletRequest httpServletRequest) {
        log.error(request.toString());
        Long customerIdFromToken = authService.getCustomerIdFromToken();
        request.setCustomerId(customerIdFromToken);
        String orderCode = orderService.createOrder(request, httpServletRequest);

        return new ApiResponse<>(orderCode);
    }

    @Operation(summary = "Staff POS checkout", description = "Endpoint for store staff to create POS orders directly at retail counters.")
    @PostMapping("/pos-checkout")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('order_create'))")
    public ApiResponse<String> staffCreateOrder(@RequestBody @Valid OrderCreateRequest request) {
        Long staffId = authService.getStaffIdFromToken();

        if (staffId == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        String orderCode = orderService.createStaffOrder(request);

        return new ApiResponse<>(orderCode);
    }

    @Operation(summary = "Get personal order history", description = "Self-service endpoint for logged-in customers to view their paginated order history.")
    @GetMapping("/my-order")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ApiResponse<PageResponse<OrderResponse>> getMyOrders(
            @Parameter(description = "Filter by specific order code") @RequestParam(required = false) String code,
            @Parameter(description = "Filter by order status (e.g., PENDING, COMPLETED, CANCELLED)") @RequestParam(required = false) String status,
            @Parameter(description = "Page number (1-based index)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderResponse> response = orderService.getCustomerOrders(code, status, page, size);

        return new ApiResponse<>(response);
    }

    @Operation(summary = "Search orders with filters", description = "Admin/Staff endpoint to filter and search orders by warehouse, code, customer email, phone, or status.")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('order_read'))")
    public ApiResponse<PageResponse<OrderResponse>> searchOrders(
            @Parameter(description = "Filter by warehouse code") @RequestParam(required = false) String warehouseCode,
            @Parameter(description = "Filter by order code") @RequestParam(required = false) String code,
            @Parameter(description = "Filter by customer email") @RequestParam(required = false) String email,
            @Parameter(description = "Filter by customer phone number") @RequestParam(required = false) String phone,
            @Parameter(description = "Filter by order status") @RequestParam(required = false) String status,
            @Parameter(description = "Page number (1-based index)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field name to sort results by") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sorting direction: 'ASC' or 'DESC'") @RequestParam(defaultValue = "DESC") String direct) {

        PageResponse<OrderResponse> response = orderService.searchOrders(
                warehouseCode, code, email, phone, status, page, size, sortBy, direct
        );
        return new ApiResponse<>(response);
    }

    @Operation(summary = "Get order detail", description = "Retrieves comprehensive information for a specific order by ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF')")
    public ApiResponse<OrderResponse> getOrderDetail(
            @Parameter(description = "ID of the order") @PathVariable Long id) {
        OrderResponse detail = orderService.getOrderDetail(id);
        return new ApiResponse<>(detail);
    }

    @Operation(summary = "Approve and split packages", description = "Approves a pending order and splits it into fulfillment package assignments.")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('order_permission'))")
    public ApiResponse<OrderResponse> approve(
            @Parameter(description = "ID of the order to approve") @PathVariable Long id,
            @RequestBody(required = false) OrderApproveRequest request) {

        OrderResponse result = orderService.approveOrder(id, request.getNote());
        return new ApiResponse<>("Successfully approved order and split into packages", result);
    }

    @Operation(summary = "Update shipment status (Internal)", description = "Internal endpoint invoked by Shipment/Delivery Service to synchronize order fulfillment status.")
    @PutMapping("/internal/{code}/updateShipment")
    public ApiResponse<Boolean> updateStatus(
            @Parameter(description = "Order code") @PathVariable String code,
            @Parameter(description = "New shipment status") @RequestParam String status) {
        orderService.updateFromShipment(code, status);
        return new ApiResponse<>(true);
    }
}