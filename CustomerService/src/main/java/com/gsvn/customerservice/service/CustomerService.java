package com.gsvn.customerservice.service;


import com.gsvn.customerservice.common.IBaseService;
import com.gsvn.customerservice.common.PageResponse;
import com.gsvn.customerservice.model.dto.request.CustomerRequest;
import com.gsvn.customerservice.model.dto.request.RegisterRequest;
import com.gsvn.customerservice.model.dto.response.CustomerResponse;


public interface CustomerService extends IBaseService<CustomerRequest,CustomerResponse,Long> {

    CustomerResponse getMyInfo();
    PageResponse<CustomerResponse> search(String kw, String sortBy,
                                          String direction, int page, int size);
    CustomerResponse updateMyInfo(CustomerRequest request);
    CustomerResponse createWithUser(CustomerRequest request,String userId);
}