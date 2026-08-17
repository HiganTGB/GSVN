package com.gsvn.hrmservice.service;

import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.request.BranchRequest;
import com.gsvn.hrmservice.model.dto.response.BranchResponse;

import java.util.List;

public interface BranchService {
    List<BranchResponse> getAllBranches();

    BranchResponse getById(Long id);

    BranchResponse create(BranchRequest request);

    BranchResponse update(Long id, BranchRequest request);

    void delete(Long id);

    PageResponse<BranchResponse> getPage(String keyword, String sortBy, String direction, int page, int size);
}