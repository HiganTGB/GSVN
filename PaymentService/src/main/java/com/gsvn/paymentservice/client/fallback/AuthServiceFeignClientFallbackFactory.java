package com.gsvn.paymentservice.client.fallback;






import com.gsvn.paymentservice.client.AuthServiceFeignClient;
import com.gsvn.paymentservice.common.ApiResponse;
import com.gsvn.paymentservice.exc.AppException;
import com.gsvn.paymentservice.exc.ErrorCode;
import com.gsvn.paymentservice.model.internal.IntrospectRequest;
import com.gsvn.paymentservice.model.internal.IntrospectResponse;
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