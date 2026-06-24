package com.gsvn.hrmservice.service;

import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.request.StaffCreateRequest;
import com.gsvn.hrmservice.model.dto.request.StaffRequest;
import com.gsvn.hrmservice.model.dto.response.StaffResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StaffService {
    StaffResponse create(StaffCreateRequest request);
    StaffResponse update(Long id, StaffRequest request);
    StaffResponse getById(Long id);
    void delete(Long id);
    PageResponse<StaffResponse> searchStaffs(
            String keyword,
            Integer warehouseId,
            Integer positionId,
            String sortBy,
            String direction,
            int page,
            int size
    );
    String uploadStaffAvatar(Long id, MultipartFile file);
    StaffResponse addAccountForStaff(Long id);
    StaffResponse getMyInfo();
    StaffResponse updateMyInfo(StaffRequest request);
    List<StaffResponse> getActiveStaff();
}