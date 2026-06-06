package com.gsvn.customerservice.controller;


import com.gsvn.customerservice.common.ApiResponse;
import com.gsvn.customerservice.model.dto.AddressCardDTO;
import com.gsvn.customerservice.service.AddressCardService;
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
public class AddressController {

    AddressCardService addressCardService;

    @GetMapping
    public ApiResponse<List<AddressCardDTO>> getMyAddresses() {
        return new ApiResponse<>(addressCardService.getMyAddresses());
    }

    @PostMapping
    public ApiResponse<AddressCardDTO> createAddress(@RequestBody @Valid AddressCardDTO dto) {
        return new ApiResponse<>(
                addressCardService.createAddress(dto));
    }

    @PatchMapping("/{id}/set-default")
    public ApiResponse<Void> setDefault(@PathVariable("id") Integer addressId) {
        addressCardService.setAsDefault(addressId);
        return new ApiResponse<>();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable("id") Integer addressId) {
        addressCardService.deleteAddress(addressId);
        return new ApiResponse<>();
    }

}