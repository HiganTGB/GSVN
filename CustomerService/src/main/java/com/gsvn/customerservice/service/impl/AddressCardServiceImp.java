package com.gsvn.customerservice.service.impl;


import com.gsvn.customerservice.converter.AddressConverter;
import com.gsvn.customerservice.exc.AppException;
import com.gsvn.customerservice.exc.ErrorCode;
import com.gsvn.customerservice.mapper.AddressMapper;
import com.gsvn.customerservice.model.dto.AddressCardDTO;
import com.gsvn.customerservice.model.entity.AddressCard;
import com.gsvn.customerservice.service.AddressCardService;
import com.gsvn.customerservice.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class AddressCardServiceImp implements AddressCardService {
    AddressMapper addressMapper;
    AddressConverter addressConverter;
    AuthenticationService authService;
    
    public AddressCardDTO createAddress(AddressCardDTO dto) {
        Long customerId = authService.getCustomerIdFromToken();
        dto.setCustomerId(customerId);
        AddressCard entity = addressConverter.toEntity(dto);
        addressMapper.insertAddress(entity);
        return addressConverter.toResponse(entity);
    }

    public List<AddressCardDTO> getMyAddresses() {
        var customerId = authService.getCustomerIdFromToken();
        return addressMapper.getAddressesByCustomerId(customerId)
                .stream()
                .map(addressConverter::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteAddress(Integer addressId) {
        Long customerId = authService.getCustomerIdFromToken();
        AddressCard address = addressMapper.getAddressById(addressId);


        if (address == null || !address.getCustomerId().equals(customerId)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        addressMapper.deleteAddress(addressId);
    }
    public void setAsDefault(Integer addressId) {
        Long customerId = authService.getCustomerIdFromToken();
        AddressCard address = addressMapper.getAddressById(addressId);
        if (address == null || !address.getCustomerId().equals(customerId)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        address.setIsDefault(true);
        addressMapper.updateAddress(address);
    }
}