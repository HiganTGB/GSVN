package com.gsvn.cartservice.client;




import com.gsvn.cartservice.common.ApiResponse;
import com.gsvn.cartservice.config.InternalFeignConfig;
import com.gsvn.cartservice.model.internal.IntrospectRequest;
import com.gsvn.cartservice.model.internal.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "auth-service",contextId = "auth",path ="/api/v1/auth", configuration = InternalFeignConfig.class )
public interface AuthServiceFeignClient {
    @PostMapping("/internal/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request);

}