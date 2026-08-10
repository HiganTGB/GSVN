package com.gsvn.hrmservice.client.fallback;

import com.gsvn.hrmservice.client.UserServiceFeignClient;
import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.exc.AppException;
import com.gsvn.hrmservice.exc.ErrorCode;
import com.gsvn.hrmservice.model.internal.*;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class UserServiceFeignClientFallbackFactory implements FallbackFactory<UserServiceFeignClient> {

    @Override
    public UserServiceFeignClient create(Throwable cause) {
        return new UserServiceFeignClient() {
            @Override
            public ApiResponse<Set<RoleResponse>> getUserRole(String userId) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Auth Service call failed when getting roles for userId: {}. Cause: {}",
                        userId, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<Set<RoleResponse>> updateUserRole(String userId, Set<Integer> roleIds) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Auth Service call failed when updating roles for userId: {}. Cause: {}",
                        userId, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<Boolean> lockUser(String userId) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Auth Service call failed when locking userId: {}. Cause: {}",
                        userId, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<Boolean> unlockUser(String userId) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Auth Service call failed when unlocking userId: {}. Cause: {}",
                        userId, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

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