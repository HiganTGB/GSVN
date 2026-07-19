package com.gsvn.orderservice.mapper;

import com.gsvn.orderservice.model.entity.OrderSagaInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface OrderSagaInstanceMapper {

    int insert(OrderSagaInstance sagaInstance);

    int updateStepAndStatus(OrderSagaInstance sagaInstance);

    OrderSagaInstance findById(@Param("sagaId") String sagaId);

    Optional<OrderSagaInstance> findByOrderId(@Param("orderId") Long orderId);

    int finishSaga(@Param("sagaId") String sagaId, @Param("status") String status);
}