package com.gsvn.accountservice.client.fallback;

import com.gsvn.accountservice.client.CustomerServiceClient;
import com.gsvn.accountservice.common.ApiResponse;
import com.gsvn.accountservice.exc.AppException;
import com.gsvn.accountservice.exc.ErrorCode;
import com.gsvn.accountservice.model.internal.CustomerRequest;
import com.gsvn.accountservice.model.internal.CustomerResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomerServiceClientFallbackFactory implements FallbackFactory<CustomerServiceClient> {

    @Override
    public CustomerServiceClient create(Throwable cause) {
        return new CustomerServiceClient() {
            @Override
            public ApiResponse<CustomerResponse> createInternalCustomer(CustomerRequest request, String userId) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Customer Service call failed for userId: {}. Cause: {}",
                        userId, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}