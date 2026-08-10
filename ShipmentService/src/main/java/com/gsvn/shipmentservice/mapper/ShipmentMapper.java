package com.gsvn.shipmentservice.mapper;

import com.gsvn.shipmentservice.model.entity.Shipment;
import com.gsvn.shipmentservice.model.entity.ShipmentItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface ShipmentMapper {



    int insertShipment(Shipment shipment);

    int insertShipmentItems(@Param("items") List<ShipmentItem> items);


    List<Shipment> findShipmentPage(
            @Param("orderCode") String orderCode,
            @Param("status") String status,
            @Param("warehouseCode") String warehouseCode,
            @Param("scheduledMonth") Integer scheduledMonth,
            @Param("scheduledYear") Integer scheduledYear,
            @Param("size") int size,
            @Param("offset") long offset
    );

    long countShipment(
            @Param("orderCode") String orderCode,
            @Param("status") String status,
            @Param("warehouseCode") String warehouseCode,
            @Param("scheduledMonth") Integer scheduledMonth,
            @Param("scheduledYear") Integer scheduledYear
    );


    int confirmReadyToPick(
            @Param("id") Long id,
            @Param("warehouseCode") String warehouseCode,
            @Param("confirmedBy") Long confirmedBy
    );

    int confirmPacked(
            @Param("id") Long id,
            @Param("totalWeight") Integer totalWeight,
            @Param("length") Integer length,
            @Param("width") Integer width,
            @Param("height") Integer height
    );

    int confirmDelivering(
            @Param("id") Long id,
            @Param("deliveryMethod") String deliveryMethod,
            @Param("partnerProvinceCode") String partnerProvinceCode,
            @Param("partnerDistrictCode") String partnerDistrictCode,
            @Param("partnerWardCode") String partnerWardCode,
            @Param("actualShipping_cost") BigDecimal actualShippingCost,
            @Param("trackingNumber") String trackingNumber
    );

    int confirmDelivered(@Param("id") Long id);

    int confirmPickupDelivered(
            @Param("id") Long id,
            @Param("confirmedBy") Long confirmedBy
    );


    int updateDeliveryMethod(
            @Param("id") Long id,
            @Param("deliveryMethod") String deliveryMethod
    );
    Shipment findById(@Param("id") Long id);
    List<Shipment> findByOrderCode(@Param("orderCode") String orderCode);

    Shipment findByShipmentCode(@Param("code") String code);

    List<ShipmentItem> findItemsByShipmentId(@Param("shipmentId") Long shipmentId);

    int changeToPickup(@Param("id") Long id);

    int countShipmentNotDeliveredByOrder(@Param("orderCode") String orderCode);
}