package com.gsvn.customerservice.service.impl;
import com.gsvn.customerservice.client.UserServiceFeignClient;
import com.gsvn.customerservice.common.PageResponse;
import com.gsvn.customerservice.converter.CustomerConverter;
import com.gsvn.customerservice.exc.AppException;
import com.gsvn.customerservice.exc.ErrorCode;
import com.gsvn.customerservice.mapper.CustomerMapper;

import com.gsvn.customerservice.model.dto.response.CustomerResponse;
import com.gsvn.customerservice.model.dto.request.CustomerRequest;
import com.gsvn.customerservice.model.entity.Customer;

import com.gsvn.customerservice.model.internal.SyncUserRequest;

import com.gsvn.customerservice.service.AuthenticationService;
import com.gsvn.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerMapper customerMapper;
    private final CustomerConverter customerConverter;
    private final AuthenticationService authenticationService;
    private final UserServiceFeignClient userServiceFeignClient;
    @Transactional(rollbackFor = Exception.class)
    public CustomerResponse create(CustomerRequest request) {
        Customer customer = customerConverter.toEntity(request);
        customerMapper.insert(customer);
        return customerConverter.toResponse(customer);
    }
    @Transactional(rollbackFor = Exception.class)
    public CustomerResponse createWithUser(CustomerRequest request,String userId) {
        var customer=customerMapper.findByEmail(request.getPhoneNumber()).orElse(null);
        if(customer==null)
        {
            customer = customerConverter.toEntity(request);
            customer.setUserId(userId);
            customerMapper.insert(customer);
            return customerConverter.toResponse(customer);
        }
        else
        {
            customer=customerConverter.toEntity(request);
            customer.setUserId(userId);
            customer.setDeletedAt(null);
            customerMapper.update(customer);
            return customerConverter.toResponse(customer);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public CustomerResponse update(Long customerId, CustomerRequest request) {
        var existingCustomer=getEntityById(customerId);
        existingCustomer=customerConverter.toEntity(request);
        customerMapper.update(existingCustomer);
        if (existingCustomer.getUserId() != null) {
            userServiceFeignClient.sync(
                    existingCustomer.getUserId(),
                    new SyncUserRequest(request.getEmail(), request.getPhoneNumber(), false)
            );
        }
        return customerConverter.toResponse(existingCustomer);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(long customerId) {
        var customer = getById(customerId);
        customerMapper.deleteById(customerId);
    }

    public CustomerResponse getById(Long customerId) {
        return customerConverter.toResponse(getEntityById(customerId));
    }
    private Customer getEntityById(Long customerId) {
        return customerMapper.findById(customerId).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @Override
    public void delete(Long id) {
        customerMapper.deleteById(id);

    }

    public PageResponse<CustomerResponse> search(String kw, String sortBy,
                                                 String direction, int page, int size) {
        int offset = (page - 1) * size;
        String sortField = switch (sortBy != null ? sortBy : "") {
            case "id" -> "customer_id";
            case "name" -> "full_name";
            case "dob" -> "dob";
            default -> "created_at";
        };
        String sortOrder = "asc".equalsIgnoreCase(direction) ? "ASC" : "DESC";
        List<Customer> customerList = customerMapper.findAdvanced(kw,sortField,sortOrder, size, offset);
        long totalElements = customerMapper.countAdvanced(kw);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        var content=customerConverter.toResponseList(customerList);
        return PageResponse.of(content,totalElements,totalPages,size);
    }
    public CustomerResponse getMyInfo()
    {
        var customerId = authenticationService.getCustomerIdFromToken();
        var info= customerMapper.findById(customerId).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        return customerConverter.toResponse(info);
    }
    @Transactional
    public CustomerResponse updateMyInfo(CustomerRequest request) {
        var customerId = authenticationService.getCustomerIdFromToken();
        var customer = customerMapper.findById(customerId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        customer = customerConverter.toEntity(request);
        customerMapper.update(customer);
        try {
            userServiceFeignClient.sync(customer.getUserId(), new SyncUserRequest(request.getEmail(), request.getPhoneNumber(), false));
        } catch (Exception ex) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return customerConverter.toResponse(customer);
    }

}