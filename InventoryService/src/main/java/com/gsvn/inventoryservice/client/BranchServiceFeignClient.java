package com.gsvn.inventoryservice.client;

import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.config.InternalFeignConfig;
import com.gsvn.inventoryservice.model.dto.response.BranchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "hrm-service",
        contextId = "branches",
        path = "/api/v1/branches",
        configuration = InternalFeignConfig.class
)
public interface BranchServiceFeignClient {

    @GetMapping
    ApiResponse<List<BranchResponse>> getAll();

    @GetMapping("/{id}")
    ApiResponse<BranchResponse> getById(@PathVariable("id") Long id);
}