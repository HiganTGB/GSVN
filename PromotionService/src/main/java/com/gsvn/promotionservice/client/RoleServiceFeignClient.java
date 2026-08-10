package com.gsvn.promotionservice.client;



import com.gsvn.promotionservice.client.fallback.RoleServiceFeignClientFallbackFactory;
import com.gsvn.promotionservice.common.ApiResponse;
import com.gsvn.promotionservice.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@FeignClient(name = "auth-service",contextId = "roles",path = "/api/v1/roles", configuration = InternalFeignConfig.class ,fallbackFactory = RoleServiceFeignClientFallbackFactory.class)
public interface RoleServiceFeignClient {
    @GetMapping("/internal/{roleId}")
    ApiResponse<Set<String>> getPermissionRoleInternal(@PathVariable Integer roleId);
}
