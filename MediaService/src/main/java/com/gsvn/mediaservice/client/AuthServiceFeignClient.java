package com.gsvn.mediaservice.client;


import com.gsvn.mediaservice.client.fallback.AuthServiceFeignClientFallbackFactory;
import com.gsvn.mediaservice.common.ApiResponse;
import com.gsvn.mediaservice.common.IntrospectRequest;
import com.gsvn.mediaservice.common.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "auth-service",contextId = "auth",path ="/api/v1/auth",fallbackFactory = AuthServiceFeignClientFallbackFactory.class)
public interface AuthServiceFeignClient {
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request);

}