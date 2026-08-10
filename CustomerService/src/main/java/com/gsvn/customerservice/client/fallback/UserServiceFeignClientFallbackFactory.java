package com.gsvn.customerservice.client.fallback;

import com.gsvn.customerservice.client.UserServiceFeignClient;
import com.gsvn.customerservice.common.ApiResponse;
import com.gsvn.customerservice.exc.AppException;
import com.gsvn.customerservice.exc.ErrorCode;
import com.gsvn.customerservice.model.internal.SyncUserRequest;
import com.gsvn.customerservice.model.internal.UserBaseRequest;
import com.gsvn.customerservice.model.internal.UserBaseResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserServiceFeignClientFallbackFactory implements FallbackFactory<UserServiceFeignClient> {

    @Override
    public UserServiceFeignClient create(Throwable cause) {
        return new UserServiceFeignClient() {
            @Override
            public ApiResponse<UserBaseResponse> create(UserBaseRequest request) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Auth Service call failed when creating user for email: {}. Cause: {}",
                        request.getEmail(), cause.getMessage());

                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<UserBaseResponse> sync(String userId, SyncUserRequest request) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Auth Service call failed when syncing user for userId: {}. Cause: {}",
                        userId, cause.getMessage());

                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}