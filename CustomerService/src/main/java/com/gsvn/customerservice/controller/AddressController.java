package com.gsvn.customerservice.controller;

import com.gsvn.customerservice.common.ApiResponse;
import com.gsvn.customerservice.model.dto.AddressCardDTO;
import com.gsvn.customerservice.service.AddressCardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/my-address-card")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Customer Address Management", description = "Self-service endpoints for managing customer delivery addresses and setting default shipping cards")
public class AddressController {

    AddressCardService addressCardService;

    @Operation(summary = "Get my saved addresses", description = "Retrieves a list of all saved delivery address cards for the currently authenticated customer.")
    @GetMapping
    public ApiResponse<List<AddressCardDTO>> getMyAddresses() {
        return new ApiResponse<>(addressCardService.getMyAddresses());
    }

    @Operation(summary = "Create new address card", description = "Adds a new delivery address card to the logged-in customer's profile.")
    @PostMapping
    public ApiResponse<AddressCardDTO> createAddress(@RequestBody @Valid AddressCardDTO dto) {
        return new ApiResponse<>(addressCardService.createAddress(dto));
    }

    @Operation(summary = "Set address as default", description = "Sets a specific saved address card as the primary/default delivery address.")
    @PatchMapping("/{id}/set-default")
    public ApiResponse<Void> setDefault(
            @Parameter(description = "ID of the target address card") @PathVariable("id") Integer addressId) {
        addressCardService.setAsDefault(addressId);
        return new ApiResponse<>();
    }

    @Operation(summary = "Delete saved address", description = "Removes a specific saved delivery address card from the customer's account.")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(
            @Parameter(description = "ID of the address card to delete") @PathVariable("id") Integer addressId) {
        addressCardService.deleteAddress(addressId);
        return new ApiResponse<>();
    }
}