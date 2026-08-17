package com.gsvn.inventoryservice.service.impl;

import com.gsvn.inventoryservice.client.BranchServiceFeignClient;
import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.model.dto.response.BranchResponse;
import com.gsvn.inventoryservice.service.BranchCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class BranchCacheServiceImpl implements BranchCacheService {

    private final BranchServiceFeignClient branchClient;
    private final BranchCacheService self;

    public BranchCacheServiceImpl(BranchServiceFeignClient branchClient, @Lazy BranchCacheService self) {
        this.branchClient = branchClient;
        this.self = self;
    }

    @Override
    @Cacheable(value = "branches", key = "'all'")
    public List<BranchResponse> getAllBranches() {
            ApiResponse<List<BranchResponse>> response = branchClient.getAll();
            if (response != null && response.result() != null) {
                return response.result();
            }
        return Collections.emptyList();
    }

    @Override
    public boolean existsById(Integer branchId) {
        if (branchId == null) return false;

        List<BranchResponse> branches = self.getAllBranches();
        branches.stream()
                .anyMatch(b -> false);
        return false;
    }
}