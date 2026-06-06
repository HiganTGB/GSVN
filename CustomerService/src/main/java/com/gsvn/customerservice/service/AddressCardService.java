package com.gsvn.customerservice.service;

import com.gsvn.customerservice.model.dto.AddressCardDTO;
import java.util.List;

public interface AddressCardService {

    AddressCardDTO createAddress(AddressCardDTO dto);


    List<AddressCardDTO> getMyAddresses();

    void deleteAddress(Integer addressId);

    void setAsDefault(Integer addressId);
}