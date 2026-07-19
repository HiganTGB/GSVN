package com.gsvn.orderservice.mapper;

import com.gsvn.orderservice.model.entity.Order;
import com.gsvn.orderservice.model.enums.OrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface OrderMapper {

    int insert(Order order);

    int updateSagaId(@Param("id") Long id, @Param("sagaId") String sagaId);

    int confirmOrder(@Param("id") Long id,
                     @Param("staffId") Long staffId,
                     @Param("note") String note);

    int updateOrderStatus(@Param("id") Long id, @Param("status") String status);

    int updatePaymentInfo(@Param("id") Long id,
                          @Param("paymentStatus") String paymentStatus,
                          @Param("amountPaid") BigDecimal amountPaid);

    Optional<Order> findById(@Param("id") Long id);

    Optional<Order> findByOrderCode(@Param("orderCode") String orderCode);

    int updateAllFields(Order order);


    List<Order> searchOrders(
            @Param("warehouseCode") String warehouseCode,
            @Param("orderCode") String orderCode,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("status") String status,
            @Param("sortBy") String sortBy,
            @Param("direct") String direct,
            @Param("size") int size,
            @Param("offset") int offset
    );
    long countSearchOrders(
            @Param("warehouseCode") String warehouseCode,
            @Param("orderCode") String orderCode,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("status") String status
    );
    int approveOrder(
            @Param("id") Long id,
            @Param("confirmedBy") Long confirmedBy,
            @Param("note") String note
    );
    OrderStatus getStatusByOrderCode(@Param("orderCode") String orderCode);

    List<Order> findByCustomerId(
            @Param("customerId") Long customerId,
            @Param("orderCode") String orderCode,
            @Param("status") String status,
            @Param("pageSize") Integer pageSize,
            @Param("offset") Integer offset
    );

    long countByCustomerId(
            @Param("customerId") Long customerId,
            @Param("orderCode") String orderCode,
            @Param("status") String status
    );
}