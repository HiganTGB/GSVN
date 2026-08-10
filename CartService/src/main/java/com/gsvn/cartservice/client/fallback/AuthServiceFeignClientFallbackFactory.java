package com.gsvn.cartservice.client.fallback;

import com.gsvn.cartservice.client.AuthServiceFeignClient;
import com.gsvn.cartservice.common.ApiResponse;
import com.gsvn.cartservice.exc.AppException;
import com.gsvn.cartservice.exc.ErrorCode;
import com.gsvn.cartservice.model.internal.IntrospectRequest;
import com.gsvn.cartservice.model.internal.IntrospectResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthServiceFeignClientFallbackFactory implements FallbackFactory<AuthServiceFeignClient> {

    @Override
    public AuthServiceFeignClient create(Throwable cause) {
        return new AuthServiceFeignClient() {
            @Override
            public ApiResponse<IntrospectResponse> authenticate(IntrospectRequest request) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Auth Service call failed during token introspection. Cause: {}",
                        cause.getMessage());
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
        };
    }
}