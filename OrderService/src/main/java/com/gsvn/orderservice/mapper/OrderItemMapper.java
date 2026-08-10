package com.gsvn.orderservice.mapper;

import com.gsvn.orderservice.model.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderItemMapper {


    int insertBatch(@Param("items") List<OrderItem> items);


    int updateFulfilledQuantity(@Param("id") Long id,
                                @Param("fulfilledQuantity") Integer fulfilledQuantity);

    int updateItemPayment(@Param("id") Long id,
                          @Param("paymentStatus") String paymentStatus,
                          @Param("amountPaid") BigDecimal amountPaid);

    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    int updateAllFields(OrderItem item);

}