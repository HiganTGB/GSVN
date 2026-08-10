package com.gsvn.productservice.client.fallback;







import com.gsvn.productservice.client.AuthServiceFeignClient;
import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.exc.AppException;
import com.gsvn.productservice.exc.ErrorCode;
import com.gsvn.productservice.model.internal.IntrospectRequest;
import com.gsvn.productservice.model.internal.IntrospectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
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