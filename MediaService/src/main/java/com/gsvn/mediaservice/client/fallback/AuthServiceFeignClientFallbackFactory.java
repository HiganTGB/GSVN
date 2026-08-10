package com.gsvn.mediaservice.client.fallback;





import com.gsvn.mediaservice.client.AuthServiceFeignClient;
import com.gsvn.mediaservice.common.ApiResponse;
import com.gsvn.mediaservice.common.IntrospectRequest;
import com.gsvn.mediaservice.common.IntrospectResponse;
import com.gsvn.mediaservice.exc.AppException;
import com.gsvn.mediaservice.exc.ErrorCode;
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