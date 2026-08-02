package com.gsvn.shipmentservice.controller;

import com.gsvn.shipmentservice.common.ApiResponse;
import com.gsvn.shipmentservice.common.PageResponse;
import com.gsvn.shipmentservice.model.dto.request.ConfirmDeliveringRequest;
import com.gsvn.shipmentservice.model.dto.request.ShipmentRequest;
import com.gsvn.shipmentservice.model.dto.response.ShipmentResponse;
import com.gsvn.shipmentservice.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentsController {

    private final ShipmentService shipmentService;

    @PostMapping("/internal")
    public ApiResponse<Void> createShipment(@RequestBody ShipmentRequest request) {
        shipmentService.createShipment(request);
        return new ApiResponse<>("Shipment created successfully", null);
    }
    @GetMapping("/byOrder/{orderCode}")
    public ApiResponse<List<ShipmentResponse>> getShipmentByOrder(@PathVariable String orderCode)
    {
        return new ApiResponse<>(shipmentService.getListByOrderCode(orderCode));
    }
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('all') or hasAuthority('shipment_read')")
    public ApiResponse<PageResponse<ShipmentResponse>> getPage(
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        var result = shipmentService.getShipmentPage(orderCode, status, warehouseCode, month, year, page, size);
        return new ApiResponse<>(result);
    }
    @GetMapping("/{id}")
    public ApiResponse<ShipmentResponse> getDetail(@PathVariable Long id) {
        return new ApiResponse<>(shipmentService.getShipmentDetail(id));
    }
    @PatchMapping("/{id}/ready-to-pick")
    @PreAuthorize("hasAuthority('all') or hasAuthority('shipment_permission')")
    public ApiResponse<Void> confirmReadyToPick(
            @PathVariable Long id,
            @RequestParam String warehouseCode){
        shipmentService.confirmReadyToPick(id, warehouseCode);
        return new ApiResponse<>("Confirmed ready to pick", null);
    }

    @PatchMapping("/{id}/packed")
    @PreAuthorize("hasAuthority('all') or hasAuthority('shipment_permission')")
    public ApiResponse<Void> confirmPacked(
            @PathVariable Long id,
            @RequestParam Integer totalWeight,
            @RequestParam Integer length,
            @RequestParam Integer width,
            @RequestParam Integer height) {
        shipmentService.confirmPacked(id, totalWeight, length, width, height);
        return new ApiResponse<>("Shipment packed successfully", null);
    }

    @PatchMapping("/{id}/delivering")
    @PreAuthorize("hasAuthority('all') or hasAuthority('shipment_permission')")
    public ApiResponse<Void> confirmDelivering(
            @PathVariable Long id,
            @RequestBody ConfirmDeliveringRequest request) {
        shipmentService.confirmDelivering(id, request);
        return new ApiResponse<>("Shipment is now delivering", null);
    }

    @PatchMapping("/{id}/delivered")
    @PreAuthorize("hasAuthority('all') or hasAuthority('shipment_permission')")
    public ApiResponse<Void> confirmDelivered(@PathVariable Long id) {
        shipmentService.confirmDelivered(id);
        return new ApiResponse<>("Shipment delivered successfully", null);
    }

    @PatchMapping("/{id}/pickup-delivered")
    @PreAuthorize("hasAuthority('all') or hasAuthority('shipment_permission')")
    public ApiResponse<Void> confirmPickupDelivered(
            @PathVariable Long id,
            @RequestParam Long confirmedBy) {
        shipmentService.confirmPickupDelivered(id, confirmedBy);
        return new ApiResponse<>("Pickup shipment completed", null);
    }

    @PutMapping("/{id}/delivery-method")
    @PreAuthorize("hasAuthority('all') or hasAuthority('shipment_permission')")
    public ApiResponse<Void> updateDeliveryMethod(
            @PathVariable Long id,
            @RequestParam String deliveryMethod) {
        shipmentService.updateDeliveryMethod(id, deliveryMethod);
        return new ApiResponse<>("Delivery method updated", null);
    }

    @PatchMapping("/{id}/change-to-pickup")
    @PreAuthorize("hasAuthority('all') or hasAuthority('shipment_permission')")
    public ApiResponse<Void> changeToPickup(@PathVariable Long id) {
        shipmentService.changeToPickup(id);
        return new ApiResponse<>("Changed to pickup successfully", null);
    }
}