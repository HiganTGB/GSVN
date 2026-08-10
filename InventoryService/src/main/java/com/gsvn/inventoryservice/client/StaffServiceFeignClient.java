package com.gsvn.inventoryservice.client;



import com.gsvn.inventoryservice.client.fallback.StaffServiceFeignClientFallbackFactory;
import com.gsvn.inventoryservice.config.InternalFeignConfig;
import com.gsvn.inventoryservice.model.internal.StaffResponse;
import com.gsvn.inventoryservice.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "hrm-service",contextId = "staff",path ="/api/v1/staffs", configuration = InternalFeignConfig.class ,fallbackFactory = StaffServiceFeignClientFallbackFactory.class)
public interface StaffServiceFeignClient {
    @GetMapping("/internal/{id}")
    ApiResponse<StaffResponse> getInternalById(@PathVariable("id") Long id);

}