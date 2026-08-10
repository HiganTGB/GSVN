package com.gsvn.promotionservice.client;



import com.gsvn.promotionservice.client.fallback.AuthServiceFeignClientFallbackFactory;
import com.gsvn.promotionservice.common.ApiResponse;
import com.gsvn.promotionservice.config.InternalFeignConfig;
import com.gsvn.promotionservice.model.dto.internal.IntrospectRequest;
import com.gsvn.promotionservice.model.dto.internal.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@FeignClient(name = "auth-service",contextId = "auth",path ="/api/v1/auth", configuration = InternalFeignConfig.class ,fallbackFactory = AuthServiceFeignClientFallbackFactory.class)
public interface AuthServiceFeignClient {
    @PostMapping("/internal/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request);

}