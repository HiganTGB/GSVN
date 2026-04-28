package com.gsvn.accountservice.client;


import com.gsvn.accountservice.common.ApiResponse;
import com.gsvn.accountservice.model.internal.CustomerRequest;
import com.gsvn.accountservice.model.internal.CustomerResponse;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(
        name = "customer-service",
        contextId = "customer",
        path = "/api/v1/customers",
        configuration = InternalFeignConfig.class
)
public interface CustomerServiceClient {
    @PostMapping("/internal/{userId}/byUser")
    public ApiResponse<CustomerResponse> createInternalCustomer(@RequestBody @Valid CustomerRequest request, @PathVariable String userId);


}