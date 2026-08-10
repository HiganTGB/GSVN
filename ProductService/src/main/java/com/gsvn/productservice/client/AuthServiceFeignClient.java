package com.gsvn.productservice.client;


import com.gsvn.productservice.client.fallback.RoleServiceFeignClientFallbackFactory;
import com.gsvn.productservice.common.ApiResponse;

import com.gsvn.productservice.config.InternalFeignConfig;
import com.gsvn.productservice.model.internal.IntrospectRequest;
import com.gsvn.productservice.model.internal.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@FeignClient(name = "auth-service",contextId = "auth",path ="/api/v1/auth", configuration = InternalFeignConfig.class,fallbackFactory = RoleServiceFeignClientFallbackFactory.class)
public interface AuthServiceFeignClient {
    @PostMapping("/internal/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request);

}