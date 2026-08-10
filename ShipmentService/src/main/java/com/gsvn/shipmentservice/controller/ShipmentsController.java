package com.gsvn.shipmentservice.controller;

import com.gsvn.shipmentservice.common.ApiResponse;
import com.gsvn.shipmentservice.common.PageResponse;
import com.gsvn.shipmentservice.model.dto.request.ConfirmDeliveringRequest;
import com.gsvn.shipmentservice.model.dto.request.ShipmentRequest;
import com.gsvn.shipmentservice.model.dto.response.ShipmentResponse;
import com.gsvn.shipmentservice.service.ShipmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipment & Delivery Management", description = "Endpoints for handling shipment fulfillment workflows, parcel state transitions (picking, packing, delivery), and delivery method configurations")
public class ShipmentsController {

    private final ShipmentService shipmentService;

    @Operation(summary = "Create shipment (Internal)", description = "Internal endpoint invoked by Order Service upon order approval to initialize shipment package records.")
    @PostMapping("/internal")
    public ApiResponse<Void> createShipment(@RequestBody ShipmentRequest request) {
        shipmentService.createShipment(request);
        return new ApiResponse<>("Shipment created successfully", null);
    }

    @Operation(summary = "Get shipments by order code", description = "Retrieves all shipment packages associated with a specific order code.")
    @GetMapping("/byOrder/{orderCode}")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF')")
    public ApiResponse<List<ShipmentResponse>> getShipmentByOrder(
            @Parameter(description = "Unique code of the order") @PathVariable String orderCode) {
        return new ApiResponse<>(shipmentService.getListByOrderCode(orderCode));
    }

    @Operation(summary = "Search shipments with filters", description = "Retrieves a paginated list of shipments filtered by order code, status, warehouse code, or month/year created.")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('shipment_read'))")
    public ApiResponse<PageResponse<ShipmentResponse>> getPage(
            @Parameter(description = "Filter by order code") @RequestParam(required = false) String orderCode,
            @Parameter(description = "Filter by shipment status (e.g., PENDING, PACKED, DELIVERING)") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by origin warehouse code") @RequestParam(required = false) String warehouseCode,
            @Parameter(description = "Filter by creation month (1-12)") @RequestParam(required = false) Integer month,
            @Parameter(description = "Filter by creation year") @RequestParam(required = false) Integer year,
            @Parameter(description = "Page number (1-based index)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {

        var result = shipmentService.getShipmentPage(orderCode, status, warehouseCode, month, year, page, size);
        return new ApiResponse<>(result);
    }

    @Operation(summary = "Get shipment detail", description = "Retrieves detailed information of a specific shipment package by ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF')")
    public ApiResponse<ShipmentResponse> getDetail(
            @Parameter(description = "ID of the shipment package") @PathVariable Long id) {
        return new ApiResponse<>(shipmentService.getShipmentDetail(id));
    }

    @Operation(summary = "Confirm ready to pick status", description = "Updates shipment status to READY_TO_PICK and assigns picking task to designated warehouse.")
    @PatchMapping("/{id}/ready-to-pick")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('shipment_permission'))")
    public ApiResponse<Void> confirmReadyToPick(
            @Parameter(description = "ID of the shipment package") @PathVariable Long id,
            @Parameter(description = "Code of the picking warehouse") @RequestParam String warehouseCode) {
        shipmentService.confirmReadyToPick(id, warehouseCode);
        return new ApiResponse<>("Confirmed ready to pick", null);
    }

    @Operation(summary = "Confirm packed status", description = "Updates shipment status to PACKED with physical dimensions and total parcel weight.")
    @PatchMapping("/{id}/packed")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('shipment_permission'))")
    public ApiResponse<Void> confirmPacked(
            @Parameter(description = "ID of the shipment package") @PathVariable Long id,
            @Parameter(description = "Total parcel weight in grams") @RequestParam Integer totalWeight,
            @Parameter(description = "Parcel length in cm") @RequestParam Integer length,
            @Parameter(description = "Parcel width in cm") @RequestParam Integer width,
            @Parameter(description = "Parcel height in cm") @RequestParam Integer height) {
        shipmentService.confirmPacked(id, totalWeight, length, width, height);
        return new ApiResponse<>("Shipment packed successfully", null);
    }

    @Operation(summary = "Confirm delivering status", description = "Handovers package to courier partner and sets status to DELIVERING.")
    @PatchMapping("/{id}/delivering")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('shipment_permission'))")
    public ApiResponse<Void> confirmDelivering(
            @Parameter(description = "ID of the shipment package") @PathVariable Long id,
            @RequestBody ConfirmDeliveringRequest request) {
        shipmentService.confirmDelivering(id, request);
        return new ApiResponse<>("Shipment is now delivering", null);
    }

    @Operation(summary = "Confirm delivered status", description = "Marks home delivery shipment as DELIVERED upon successful customer drop-off.")
    @PatchMapping("/{id}/delivered")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('shipment_permission'))")
    public ApiResponse<Void> confirmDelivered(
            @Parameter(description = "ID of the shipment package") @PathVariable Long id) {
        shipmentService.confirmDelivered(id);
        return new ApiResponse<>("Shipment delivered successfully", null);
    }

    @Operation(summary = "Confirm pickup order delivered", description = "Marks store self-pickup shipment as DELIVERED when handed over to customer in-store.")
    @PatchMapping("/{id}/pickup-delivered")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('shipment_permission'))")
    public ApiResponse<Void> confirmPickupDelivered(
            @Parameter(description = "ID of the shipment package") @PathVariable Long id,
            @Parameter(description = "ID of the staff member confirming pickup") @RequestParam Long confirmedBy) {
        shipmentService.confirmPickupDelivered(id, confirmedBy);
        return new ApiResponse<>("Pickup shipment completed", null);
    }

    @Operation(summary = "Update delivery method", description = "Updates delivery method configuration for a specific shipment package.")
    @PutMapping("/{id}/delivery-method")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('shipment_permission'))")
    public ApiResponse<Void> updateDeliveryMethod(
            @Parameter(description = "ID of the shipment package") @PathVariable Long id,
            @Parameter(description = "New delivery method (e.g., EXPRESS, STANDARD, PICKUP)") @RequestParam String deliveryMethod) {
        shipmentService.updateDeliveryMethod(id, deliveryMethod);
        return new ApiResponse<>("Delivery method updated", null);
    }

    @Operation(summary = "Switch delivery mode to store pickup", description = "Converts shipment delivery method from standard delivery to store self-pickup.")
    @PatchMapping("/{id}/change-to-pickup")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('shipment_permission'))")
    public ApiResponse<Void> changeToPickup(
            @Parameter(description = "ID of the shipment package") @PathVariable Long id) {
        shipmentService.changeToPickup(id);
        return new ApiResponse<>("Changed to pickup successfully", null);
    }
}