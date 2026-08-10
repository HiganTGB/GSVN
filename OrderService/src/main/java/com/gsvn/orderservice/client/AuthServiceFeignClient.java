package com.gsvn.orderservice.client;



import com.gsvn.orderservice.client.fallback.AuthServiceFeignClientFallbackFactory;
import com.gsvn.orderservice.common.ApiResponse;
import com.gsvn.orderservice.config.InternalFeignConfig;
import com.gsvn.orderservice.model.internal.IntrospectRequest;
import com.gsvn.orderservice.model.internal.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@FeignClient(name = "auth-service",contextId = "auth",path ="/api/v1/auth", configuration = InternalFeignConfig.class,fallbackFactory = AuthServiceFeignClientFallbackFactory.class)
public interface AuthServiceFeignClient {
    @PostMapping("/internal/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request);

}