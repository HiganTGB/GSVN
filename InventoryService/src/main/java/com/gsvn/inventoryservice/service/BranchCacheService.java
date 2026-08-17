package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.model.dto.response.BranchResponse;

import java.util.List;

public interface BranchCacheService {

    List<BranchResponse> getAllBranches();

    boolean existsById(Integer branchId);
}