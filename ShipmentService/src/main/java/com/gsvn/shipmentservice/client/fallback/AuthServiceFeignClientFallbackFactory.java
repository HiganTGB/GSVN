package com.gsvn.shipmentservice.client.fallback;









import com.gsvn.shipmentservice.client.AuthServiceFeignClient;
import com.gsvn.shipmentservice.common.ApiResponse;
import com.gsvn.shipmentservice.exc.AppException;
import com.gsvn.shipmentservice.exc.ErrorCode;
import com.gsvn.shipmentservice.model.internal.IntrospectRequest;
import com.gsvn.shipmentservice.model.internal.IntrospectResponse;
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