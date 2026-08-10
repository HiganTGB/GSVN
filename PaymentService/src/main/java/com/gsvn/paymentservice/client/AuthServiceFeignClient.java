package com.gsvn.paymentservice.client;



import com.gsvn.paymentservice.client.fallback.AuthServiceFeignClientFallbackFactory;
import com.gsvn.paymentservice.common.ApiResponse;
import com.gsvn.paymentservice.config.InternalFeignConfig;
import com.gsvn.paymentservice.model.internal.IntrospectRequest;
import com.gsvn.paymentservice.model.internal.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@FeignClient(name = "auth-service",contextId = "auth",path ="/api/v1/auth", configuration = InternalFeignConfig.class,fallbackFactory = AuthServiceFeignClientFallbackFactory.class)
public interface AuthServiceFeignClient {
    @PostMapping("/internal/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request);

}