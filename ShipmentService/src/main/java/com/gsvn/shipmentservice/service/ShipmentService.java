package com.gsvn.shipmentservice.service;
import com.gsvn.shipmentservice.common.PageResponse;
import com.gsvn.shipmentservice.model.dto.request.ConfirmDeliveringRequest;
import com.gsvn.shipmentservice.model.dto.response.ShipmentResponse;
import com.gsvn.shipmentservice.model.dto.request.ShipmentRequest;
import java.util.List;

public interface ShipmentService {

    void createShipment(ShipmentRequest request);
    PageResponse<ShipmentResponse> getShipmentPage(
            String orderCode, String status, String warehouseCode,
            Integer month, Integer year, int page, int size);
    void confirmReadyToPick(Long id, String warehouseCode);
    void confirmPacked(Long id, Integer weight, Integer l, Integer w, Integer h);
    void confirmDelivering(Long id, ConfirmDeliveringRequest request);
    void confirmDelivered(Long id);
    void updateDeliveryMethod(Long id, String deliveryMethod);
    void changeToPickup(Long id);
    ShipmentResponse getShipmentDetail(Long id);
    void confirmPickupDelivered(Long id, Long confirmedBy);
    List<ShipmentResponse> getListByOrderCode(String orderCode);

}