package com.gsvn.shipmentservice.service;

import com.gsvn.shipmentservice.client.InventoryClient;
import com.gsvn.shipmentservice.client.OrderClient;
import com.gsvn.shipmentservice.client.PaymentClient;
import com.gsvn.shipmentservice.common.ApiResponse;
import com.gsvn.shipmentservice.common.PageResponse;
import com.gsvn.shipmentservice.converter.GHNShipmentConverter;
import com.gsvn.shipmentservice.converter.ShipmentConverter;
import com.gsvn.shipmentservice.exc.AppException;
import com.gsvn.shipmentservice.exc.ErrorCode;
import com.gsvn.shipmentservice.mapper.ShipmentMapper;
import com.gsvn.shipmentservice.mapper.WarehousePartnerMapper;
import com.gsvn.shipmentservice.model.dto.GHNOrderRequest;
import com.gsvn.shipmentservice.model.dto.internal.InventoryUpdateRequest;
import com.gsvn.shipmentservice.model.dto.internal.OrderStatus;
import com.gsvn.shipmentservice.model.dto.internal.PaymentRequest;
import com.gsvn.shipmentservice.model.dto.request.ConfirmDeliveringRequest;
import com.gsvn.shipmentservice.model.dto.response.ShipmentResponse;
import com.gsvn.shipmentservice.model.entity.Shipment;
import com.gsvn.shipmentservice.model.entity.ShipmentItem;
import com.gsvn.shipmentservice.model.entity.ShipmentStatus;
import com.gsvn.shipmentservice.model.dto.request.ShipmentRequest;
import com.gsvn.shipmentservice.model.entity.WarehousePartner;
import com.gsvn.shipmentservice.model.internal.WarehouseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentMapper shipmentMapper;
    private final ShipmentConverter shipmentConverter;
    private final GHNShipmentConverter ghnPartner;
    private final WarehousePartnerService warehousePartnerService;
    private final AuthenticationService authenticationService;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final OrderClient orderClient;

    @Transactional
    public void createShipment(ShipmentRequest request) {
        Shipment shipment = shipmentConverter.toEntity(request);
        shipmentMapper.insertShipment(shipment);
        List<ShipmentItem> items = request.getItems().stream()
                .map(i -> ShipmentItem.builder()
                        .shipmentId(shipment.getId())
                        .skuCode(i.getSkuCode())
                        .productName(i.getProductName())
                        .quantity(i.getQuantity())
                        .build())
                .toList();
        if (!items.isEmpty()) {
            shipmentMapper.insertShipmentItems(items);
        }
    }
    public PageResponse<ShipmentResponse> getShipmentPage(
            String orderCode, String status, String warehouseCode,
            Integer month, Integer year, int page, int size) {

        int offset = (page - 1) * size;


        List<Shipment> shipments = shipmentMapper.findShipmentPage(
                orderCode, status, warehouseCode, month, year, size, offset
        );

        long total = shipmentMapper.countShipment(orderCode, status, warehouseCode, month, year);

        List<ShipmentResponse> responseList = shipments.stream()
                .map(s -> shipmentConverter.toResponse(s, s.getOrderCode()))
                .toList();

        return PageResponse.of(responseList, total, page, size);
    }
    @Transactional
    public void confirmReadyToPick(Long id, String warehouseCode) {
        var staffId = authenticationService.getStaffIdFromToken();
        if (staffId == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        int updated = shipmentMapper.confirmReadyToPick(id, warehouseCode, staffId);
        if (updated == 0) {
            throw new AppException(ErrorCode.SHIPMENT_NOT_FOUND);
        }
        WarehouseResponse warehouse = inventoryClient.getByCode(warehouseCode).result();

        Shipment shipment = shipmentMapper.findById(id);
        List<ShipmentItem> items = shipmentMapper.findItemsByShipmentId(id);

        for (ShipmentItem item : items) {
            inventoryClient.processReadyToPick(InventoryUpdateRequest.builder()
                    .skuCode(item.getSkuCode())
                    .warehouseId(warehouse.getId())
                    .quantity(item.getQuantity())
                    .build());
        }
    }

    @Transactional
    public void confirmPacked(Long id, Integer weight, Integer l, Integer w, Integer h) {

        Shipment shipment = shipmentMapper.findById(id);
        if (shipment == null) throw new AppException(ErrorCode.SHIPMENT_NOT_FOUND);

        List<ShipmentItem> items = shipmentMapper.findItemsByShipmentId(id);
        WarehouseResponse warehouse = inventoryClient.getByCode(shipment.getWarehouseCode()).result();

        int updated = shipmentMapper.confirmPacked(id, weight, l, w, h);
        if (updated == 0) throw new AppException(ErrorCode.SHIPMENT_NOT_FOUND);

        for (ShipmentItem item : items) {
            inventoryClient.processPacked(InventoryUpdateRequest.builder()
                    .skuCode(item.getSkuCode())
                    .warehouseId(warehouse.getId())
                    .quantity(item.getQuantity())
                    .build());
        }
    }
    @Transactional
    public void confirmDelivering(Long id, ConfirmDeliveringRequest request) {
        int updated = shipmentMapper.confirmDelivering(
                id,
                request.getDeliveryMethod(),
                request.getPartnerProvinceCode(),
                request.getPartnerDistrictCode(),
                request.getPartnerWardCode(),
                request.getActualShippingCost(),
                request.getTrackingNumber()
        );
        if (updated == 0) throw new AppException(ErrorCode.SHIPMENT_NOT_FOUND);
    }
    private void checkSendPaymentAndCheckDeliveredAll(Shipment shipment)
    {
        PaymentRequest request = PaymentRequest.builder()
                .shipmentCode(shipment.getShipmentCode())
                .orderCode(shipment.getOrderCode())
                .referenceId(shipment.getTrackingNumber())
                .amount(shipment.getTotalCodAmount())
                .provider(shipment.getDeliveryMethod())
                .externalTransactionId(shipment.getWarehouseCode()+shipment.getOrderCode())
                .paymentMethod("BALANCE")
                .paymentType("CASH")
                .confirmedBy(shipment.getConfirmedBy())
                .build();
        Boolean response = paymentClient.confirmCod(request).result();
        int leftAfter= shipmentMapper.countShipmentNotDeliveredByOrder(shipment.getOrderCode());
        if(leftAfter==0) {
            orderClient.updateStatus(shipment.getOrderCode(), OrderStatus.COMPLETED.name());
        }
        else {
            orderClient.updateStatus(shipment.getOrderCode(), OrderStatus.PROCESSING.name());
        }
    }

    @Transactional
    public void confirmDelivered(Long id) {
        Shipment shipment = shipmentMapper.findById(id);
        int updated = shipmentMapper.confirmDelivered(id);
        if (updated == 0) {
            throw new AppException(ErrorCode.SHIPMENT_NOT_FOUND);
        }
        checkSendPaymentAndCheckDeliveredAll(shipment);

    }
    @Transactional
    public void updateDeliveryMethod(Long id, String deliveryMethod) {
        int updated = shipmentMapper.updateDeliveryMethod(id, deliveryMethod);
        if (updated == 0) {
            throw new AppException(ErrorCode.SHIPMENT_NOT_FOUND);
        }
    }
    @Transactional
    public void changeToPickup(Long id) {
        int updated = shipmentMapper.changeToPickup(id);
        if (updated == 0) {
            throw new AppException(ErrorCode.SHIPMENT_NOT_FOUND);
        }
    }
    public ShipmentResponse getShipmentDetail(Long id) {

        Shipment shipment = shipmentMapper.findById(id);

        if (shipment == null) {
            throw new AppException(ErrorCode.SHIPMENT_NOT_FOUND);
        }
        return shipmentConverter.toResponse(shipment, shipment.getOrderCode());
    }
    @Transactional
    public void confirmPickupDelivered(Long id, Long confirmedBy) {
        Shipment shipment = shipmentMapper.findById(id);
        int updated = shipmentMapper.confirmPickupDelivered(id, confirmedBy);
        if (updated == 0) throw new AppException(ErrorCode.SHIPMENT_NOT_FOUND);
        checkSendPaymentAndCheckDeliveredAll(shipment);
    }
    public List<ShipmentResponse> getListByOrderCode(String orderCode)
    {
        List<Shipment> shipments= shipmentMapper.findByOrderCode(orderCode);
        return shipmentConverter.toResponseList(shipments);
    }

}