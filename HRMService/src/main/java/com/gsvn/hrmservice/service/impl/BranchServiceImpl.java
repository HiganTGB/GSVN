package com.gsvn.hrmservice.service.impl;

import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.converter.BranchConverter;
import com.gsvn.hrmservice.exc.AppException;
import com.gsvn.hrmservice.exc.DuplicateResourceException;
import com.gsvn.hrmservice.exc.ErrorCode;
import com.gsvn.hrmservice.mapper.BranchMapper;
import com.gsvn.hrmservice.model.dto.request.BranchRequest;
import com.gsvn.hrmservice.model.dto.response.BranchResponse;
import com.gsvn.hrmservice.model.entity.Branch;
import com.gsvn.hrmservice.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchMapper branchMapper;
    private final BranchConverter converter;

    @Override
    @Cacheable(value = "branches", key = "'all'")
    public List<BranchResponse> getAllBranches() {
        return converter.toResponseList(branchMapper.findAll());
    }

    @Override
    @Cacheable(value = "branches", key = "#id")
    public BranchResponse getById(Long id) {
        Branch branch = getEntityById(id);
        return converter.toResponse(branch);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "branches", key = "'all'"),
            @CacheEvict(value = "branches_page", allEntries = true)
    })
    public BranchResponse create(BranchRequest request) {
        if (branchMapper.existByCode(request.getBranchCode())) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY, "branchCode");
        }
        Branch branch = converter.toEntity(request);
        branchMapper.insert(branch);
        return converter.toResponse(branch);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "branches", key = "#id"),
            @CacheEvict(value = "branches", key = "'all'"),
            @CacheEvict(value = "branches_page", allEntries = true)
    })
    public BranchResponse update(Long id, BranchRequest request) {
        Branch existingBranch = getEntityById(id);

        if (!existingBranch.getBranchCode().equals(request.getBranchCode())
                && branchMapper.existByCode(request.getBranchCode())) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY, "branchCode");
        }

        existingBranch = converter.updateEntity(existingBranch, request);
        branchMapper.update(existingBranch);
        return converter.toResponse(existingBranch);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "branches", allEntries = true),
            @CacheEvict(value = "branches_page", allEntries = true)
    })
    public void delete(Long id) {
        getEntityById(id);
        branchMapper.deleteById(id);
    }

    private Branch getEntityById(Long id) {
        return branchMapper.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
    }

    @Override
    @Cacheable(value = "branches_page",
            key = "#keyword + ':' + #sortBy + ':' + #direction + ':' + #page + ':' + #size")
    public PageResponse<BranchResponse> getPage(String keyword, String sortBy, String direction, int page, int size) {
        page = Math.max(1, page);
        long offset = (long) (page - 1) * size;
        List<Branch> entities = branchMapper.findAdvanced(keyword, sortBy, direction, size, offset);
        long totalElements = branchMapper.countAdvanced(keyword);

        List<BranchResponse> content = converter.toResponseList(entities);

        return PageResponse.of(content, totalElements, page, size);
    }
}