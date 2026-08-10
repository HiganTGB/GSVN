package com.gsvn.customerservice.converter;

import com.gsvn.customerservice.common.IBaseConverter;
import com.gsvn.customerservice.model.dto.AddressCardDTO;
import com.gsvn.customerservice.model.entity.AddressCard;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AddressConverter implements IBaseConverter<AddressCard, AddressCardDTO, AddressCardDTO> {

    @Override
    public AddressCardDTO toResponse(AddressCard entity) {
        if (entity == null) {
            return null;
        }

        return AddressCardDTO.builder()
                .addressId(entity.getAddressId())
                .customerId(entity.getCustomerId())
                .receiverName(entity.getReceiverName())
                .receiverPhone(entity.getReceiverPhone())
                .provinceCode(entity.getProvinceCode())
                .wardCode(entity.getWardCode())
                .addressDetail(entity.getAddressDetail())
                .isDefault(entity.getIsDefault())
                .build();
    }

    @Override
    public AddressCard toEntity(AddressCardDTO dto) {
        if (dto == null) {
            return null;
        }

        return AddressCard.builder()
                .addressId(dto.getAddressId())
                .customerId(dto.getCustomerId())
                .receiverName(dto.getReceiverName())
                .receiverPhone(dto.getReceiverPhone())
                .provinceCode(dto.getProvinceCode())
                .wardCode(dto.getWardCode())
                .addressDetail(dto.getAddressDetail())
                .isDefault(dto.getIsDefault())
                .build();
    }

    public List<AddressCardDTO> toResponseList(List<AddressCard> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }
}