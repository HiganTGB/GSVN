package com.gsvn.shipmentservice.converter;

import com.gsvn.shipmentservice.model.dto.request.ShipmentRequest;
import com.gsvn.shipmentservice.model.dto.response.ShipmentResponse;
import com.gsvn.shipmentservice.model.entity.Shipment;
import com.gsvn.shipmentservice.model.entity.ShipmentStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ShipmentConverter {
    public ShipmentResponse toResponse(Shipment entity, String orderCode) {
        if (entity == null) return null;

        return ShipmentResponse.builder()
                .id(entity.getId())
                .orderCode(orderCode)
                .shipmentCode(entity.getShipmentCode())
                .warehouseCode(entity.getWarehouseCode())
                .deliveryMethod(entity.getDeliveryMethod())
                .receiverEmail(entity.getReceiverEmail())
                .status(ShipmentStatus.valueOf(entity.getStatus()))
                .scheduledDate(entity.getScheduledDate())
                .totalCodAmount(entity.getTotalCodAmount())
                .actualShippingCost(entity.getActualShippingCost())
                .trackingNumber(entity.getTrackingNumber())
                .totalWeight(entity.getTotalWeight())
                .length(entity.getLength())
                .width(entity.getWidth())
                .height(entity.getHeight())
                .receiverName(entity.getReceiverName())
                .receiverPhone(entity.getReceiverPhone())
                .provinceCode(entity.getProvinceCode())
                .wardCode(entity.getWardCode())
                .addressDetail(entity.getAddressDetail())
                .customerNote(entity.getCustomerNote())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    public List<ShipmentResponse> toResponseList(List<Shipment> entities) {
        if (entities == null) return null;
        return entities.stream().map(x->toResponse(x,x.getOrderCode())).collect(Collectors.toList());
    }
    public Shipment toEntity(ShipmentRequest request) {
        if (request == null) {
            return null;
        }

        return Shipment.builder()
                .orderCode(request.getOrderCode())
                .warehouseCode(request.getWarehouseCode())
                .deliveryMethod(request.getDeliveryMethod())
                .scheduledDate(request.getScheduledDate())
                .totalCodAmount(request.getTotalCodAmount())
                .shipmentCode(UUID.randomUUID().toString())
                // Thông tin người nhận
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .receiverEmail(request.getReceiverEmail())
                .provinceCode(request.getProvinceCode())
                .wardCode(request.getWardCode())
                .addressDetail(request.getAddressDetail())
                .customerNote(request.getCustomerNote())


                .status(ShipmentStatus.ON_HOLD.name())
                .build();
    }
}