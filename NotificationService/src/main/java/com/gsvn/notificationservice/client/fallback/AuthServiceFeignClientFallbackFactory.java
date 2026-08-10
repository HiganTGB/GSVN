package com.gsvn.notificationservice.client.fallback;




import com.gsvn.notificationservice.client.AuthServiceFeignClient;
import com.gsvn.notificationservice.common.ApiResponse;
import com.gsvn.notificationservice.exc.AppException;
import com.gsvn.notificationservice.exc.ErrorCode;
import com.gsvn.notificationservice.model.internal.IntrospectRequest;
import com.gsvn.notificationservice.model.internal.IntrospectResponse;
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