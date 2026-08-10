package com.gsvn.shipmentservice.client;



import com.gsvn.shipmentservice.client.fallback.RoleServiceFeignClientFallbackFactory;
import com.gsvn.shipmentservice.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@FeignClient(name = "auth-service",contextId = "roles",fallbackFactory = RoleServiceFeignClientFallbackFactory.class,path = "/api/v1/roles")
public interface RoleServiceFeignClient {
    @GetMapping("/internal/{roleId}")
    ApiResponse<Set<String>> getPermissionRoleInternal(@PathVariable Integer roleId);
}
