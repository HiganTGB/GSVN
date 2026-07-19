package com.gsvn.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsvn.orderservice.client.MediaClient;
import com.gsvn.orderservice.client.ShipmentFeignClient;
import com.gsvn.orderservice.common.ApiResponse;
import com.gsvn.orderservice.common.PageResponse;
import com.gsvn.orderservice.converter.OrderConverter;
import com.gsvn.orderservice.exc.AppException;
import com.gsvn.orderservice.exc.ErrorCode;
import com.gsvn.orderservice.mapper.MessageLogMapper;
import com.gsvn.orderservice.mapper.OrderItemMapper;
import com.gsvn.orderservice.mapper.OrderMapper;
import com.gsvn.orderservice.mapper.OrderSagaInstanceMapper;
import com.gsvn.orderservice.model.dto.internal.ShipmentRequest;
import com.gsvn.orderservice.model.dto.request.OrderCreateRequest;
import com.gsvn.orderservice.model.dto.response.OrderResponse;
import com.gsvn.orderservice.model.entity.*;
import com.gsvn.orderservice.model.enums.OrderStatus;
import com.gsvn.orderservice.model.enums.PaymentStatus;
import com.gsvn.orderservice.model.saga.SagaStatus;
import com.gsvn.orderservice.model.saga.SagaStep;
import com.gsvn.orderservice.queue.message.SkuValidateRequestMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderConverter orderConverter;
    private final MessageLogMapper logMapper;
    private final ObjectMapper objectMapper;
    private final AuthenticationService authenticationService;
    private final ShipmentFeignClient shipmentFeignClient;
    private final MediaClient mediaClient;



    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-ForwardED-FOR");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }


    public PageResponse<OrderResponse> searchOrders(
            String warehouseCode, String code, String email, String phone, String status,
            int page, int size, String sortBy, String direct) {

        int offset = (page - 1) * size;
        if (offset < 0) offset = 0;

        List<Order> orders = orderMapper.searchOrders(warehouseCode, code, email, phone, status, sortBy, direct, size, offset);
        long total = orderMapper.countSearchOrders(warehouseCode, code, email, phone, status);

        List<OrderResponse> responseList = orderConverter.toResponseList(orders);

        return PageResponse.of(responseList, total, page, size);
    }


    public OrderResponse getOrderDetail(Long orderId) {
        Order order = orderMapper.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));

        List<OrderItem> items = orderItemMapper.findByOrderId(orderId);

        if (items != null && !items.isEmpty()) {
            List<String> paths = items.stream()
                    .map(OrderItem::getImageUrl)
                    .filter(StringUtils::hasText)
                    .distinct()
                    .collect(Collectors.toList());

            if (!paths.isEmpty()) {
                try {
                    var response = mediaClient.getPreviewUrls(paths);

                    if (response != null && response.result() != null) {
                        Map<String, String> urlMap = response.result();
                        items.forEach(item -> {
                            String originalPath = item.getImageUrl();
                            if (StringUtils.hasText(originalPath) && urlMap.containsKey(originalPath)) {
                                item.setImageUrl(urlMap.get(originalPath));
                            }
                        });
                    }
                } catch (Exception e) {
                    log.error("Lỗi khi lấy Preview URLs từ Media Service cho Order: {}", orderId, e);
                }
            }
        }
        return orderConverter.toResponse(order, items);
    }


    @Transactional
    public OrderResponse approveOrder(Long orderId, String note) {
        Long confirmedBy = authenticationService.getStaffIdFromToken();
        Order order = orderMapper.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ITEM_NOT_EXISTED));

        orderMapper.approveOrder(orderId, confirmedBy, note);

        List<OrderItem> allItems = orderItemMapper.findByOrderId(orderId);
       splitShipments(order,allItems);

        return getOrderDetail(orderId);
    }
    private void splitShipments(Order order, List<OrderItem> items) {
        LocalDate today = LocalDate.now();


        Map<YearMonth, List<OrderItem>> groups = items.stream()
                .collect(Collectors.groupingBy(item -> {
                    LocalDate date = item.getScheduledDate() != null ? item.getScheduledDate() : today;
                    return YearMonth.from(date);
                }));


        List<YearMonth> sortedMonths = groups.keySet().stream().sorted().toList();

        for (int i = 0; i < sortedMonths.size(); i++) {
            YearMonth groupMonth = sortedMonths.get(i);
            List<OrderItem> itemList = groups.get(groupMonth);


            LocalDate displayDate = groupMonth.equals(YearMonth.from(today)) ? today : groupMonth.atDay(1);

            BigDecimal codAmount = BigDecimal.ZERO;

            if (!PaymentStatus.FULLY_PAID.equals(order.getPaymentStatus())) {
                BigDecimal subTotal = itemList.stream()
                        .map(OrderItem::getSubPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                codAmount = subTotal;
                if (i == 0) {
                    BigDecimal discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
                    codAmount = codAmount.subtract(discount);
                }
            }

            ShipmentRequest shipmentReq = ShipmentRequest.builder()
                    .orderId(order.getId())
                    .orderCode(order.getOrderCode())
                    .warehouseCode(order.getWarehouseCode())
                    .deliveryMethod(String.valueOf(order.getDeliveryMethod()))
                    .scheduledDate(displayDate)
                    .receiverName(order.getReceiverName())
                    .receiverPhone(order.getReceiverPhone())
                    .receiverEmail(order.getReceiverEmail())
                    .provinceCode(order.getProvinceCode())
                    .wardCode(order.getWardCode())
                    .addressDetail(order.getAddressDetail())
                    .customerNote(order.getCustomerNote())
                    .totalCodAmount(codAmount.max(BigDecimal.ZERO))
                    .items(itemList.stream().map(item -> ShipmentRequest.ItemRequest.builder()
                            .skuCode(item.getSkuCode())
                            .orderItemId(item.getId())
                            .quantity(item.getQuantity())
                            .productName(item.getProductName())
                            .build()).toList())
                    .build();

            shipmentFeignClient.createShipment(shipmentReq);
        }
    }
    public PageResponse<OrderResponse> getCustomerOrders(
            String orderCode, String status, int page, int size) {

        Long customerId = authenticationService.getCustomerIdFromToken();
        if (customerId == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        int offset = (page - 1) * size;
        if (offset < 0) offset = 0;

        List<Order> orders = orderMapper.findByCustomerId(customerId, orderCode, status, size, offset);
        long total = orderMapper.countByCustomerId(customerId, orderCode, status);

        List<OrderResponse> responseList = orderConverter.toResponseList(orders);

        return PageResponse.of(responseList, total, page, size);
    }
    public void updateFromShipment(String orderCode,String value)
    {
        List<String> allowedStatuses = List.of(OrderStatus.PROCESSING.name(),OrderStatus.COMPLETED.name());

        if (!allowedStatuses.contains(value)) {
            throw new AppException(ErrorCode.NOT_ALLOW);
        }else
        {
            Order order=orderMapper.findByOrderCode(orderCode).orElseThrow(()->new AppException(ErrorCode.ITEM_NOT_EXISTED));
            if(order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                return;
            }
            if(OrderStatus.PROCESSING.equals(order.getOrderStatus())||OrderStatus.CONFIRMED.equals(order.getOrderStatus())) {
            int updated= orderMapper.updateOrderStatus(order.getId(), OrderStatus.valueOf(value).name());
            }
        }

    }



}