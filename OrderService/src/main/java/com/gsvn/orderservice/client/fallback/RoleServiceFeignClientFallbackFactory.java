package com.gsvn.orderservice.client.fallback;



import com.gsvn.orderservice.client.RoleServiceFeignClient;
import com.gsvn.orderservice.common.ApiResponse;
import com.gsvn.orderservice.exc.AppException;
import com.gsvn.orderservice.exc.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class RoleServiceFeignClientFallbackFactory implements FallbackFactory<RoleServiceFeignClient> {

    @Override
    public RoleServiceFeignClient create(Throwable cause) {
        return new RoleServiceFeignClient() {
            @Override
            public ApiResponse<Set<String>> getPermissionRoleInternal(Integer roleId) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Auth Service call failed when fetching permissions for roleId: {}. Cause: {}",
                        roleId, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}