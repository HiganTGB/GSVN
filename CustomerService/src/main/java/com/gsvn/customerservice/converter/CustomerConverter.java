package com.gsvn.customerservice.converter;

import com.gsvn.customerservice.common.IBaseConverter;
import com.gsvn.customerservice.model.dto.request.CustomerRequest;
import com.gsvn.customerservice.model.dto.request.RegisterRequest;
import com.gsvn.customerservice.model.entity.Customer;
import com.gsvn.customerservice.model.dto.response.CustomerResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerConverter implements IBaseConverter<Customer, CustomerRequest,CustomerResponse> {


    public CustomerResponse toResponse(Customer entity) {
        if (entity == null) {
            return null;
        }

        return CustomerResponse.builder()
                .customerId(entity.getCustomerId())
                .userId(entity.getUserId())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .gender(entity.getGender())
                .dob(entity.getDob())
                .phoneNumber(entity.getPhoneNumber())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }


    public Customer toEntity(CustomerRequest request) {
        if (request == null) {
            return null;
        }

        Customer entity = new Customer();
        entity.setEmail(request.getEmail());
        entity.setFullName(request.getFullName());
        entity.setGender(request.getGender());
        entity.setDob(request.getDob());
        entity.setPhoneNumber(request.getPhoneNumber());
        return entity;
    }
    public Customer toEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }

        Customer entity = new Customer();
        entity.setEmail(request.getEmail());
        entity.setFullName(request.getFullName());
        entity.setGender(request.getGender());
        entity.setDob(request.getDob());
        entity.setPhoneNumber(request.getPhoneNumber());
        return entity;
    }
    public Customer updateEntity(Customer entity, CustomerRequest request) {
        if (request == null || entity == null) return entity;

        entity.setEmail(request.getEmail());
        entity.setFullName(request.getFullName());
        entity.setGender(request.getGender());
        entity.setDob(request.getDob());
        entity.setPhoneNumber(request.getPhoneNumber());

        return entity;
    }

}