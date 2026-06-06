package com.gsvn.customerservice.controller;


import com.gsvn.customerservice.common.ApiResponse;
import com.gsvn.customerservice.common.PageResponse;
import com.gsvn.customerservice.model.dto.response.CustomerResponse;
import com.gsvn.customerservice.model.dto.request.CustomerRequest;

import com.gsvn.customerservice.service.impl.CustomerServiceImpl;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerController {
    CustomerServiceImpl customerService;

    @PostMapping
    public ApiResponse<CustomerResponse> createCustomer(@RequestBody @Valid CustomerRequest request) {
        return new ApiResponse<>(customerService.create(request));
    }
    @PostMapping("/internal/{userId}/byUser")
    public ApiResponse<CustomerResponse> createInternalCustomer(@RequestBody @Valid CustomerRequest request, @PathVariable String userId) {
        return new ApiResponse<>(customerService.createWithUser(request,userId));
    }

    @PutMapping("/{customerId}")
    public ApiResponse<CustomerResponse> updateCustomer(
            @PathVariable long customerId,
            @RequestBody @Valid CustomerRequest request) {
        return new ApiResponse<>(customerService.update(customerId, request));
    }

    @GetMapping("/{customerId}")
    public ApiResponse<CustomerResponse> getCustomer(@PathVariable long customerId) {
        return new ApiResponse<>(customerService.getById(customerId));
    }

    @DeleteMapping("/{customerId}")
    public ApiResponse<Void> deleteCustomer(@PathVariable long customerId) {
        customerService.delete(customerId);
        return new ApiResponse<>();
    }
    @GetMapping("/my-info")
    public ApiResponse<CustomerResponse> getMyInfo()
    {
        return new ApiResponse<>(customerService.getMyInfo());
    }
    @PutMapping("/my-info")
    public ApiResponse<CustomerResponse> updateMyInfo(@RequestBody @Valid CustomerRequest request)
    {
        return new ApiResponse<>(customerService.updateMyInfo(request));
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<CustomerResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return new ApiResponse<>(customerService.search(
                keyword,
                sortBy,
                direction,
                page,
                size
        ));
    }
}