package com.gsvn.customerservice.client;




import com.gsvn.customerservice.common.ApiResponse;
import com.gsvn.customerservice.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@FeignClient(name = "auth-service",contextId = "roles",path = "/api/v1/roles", configuration = InternalFeignConfig.class)
public interface RoleServiceFeignClient {
    @GetMapping("/internal/{roleId}")
    ApiResponse<Set<String>> getPermissionRoleInternal(@PathVariable Integer roleId);
}
