package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.request.StaffCreateRequest;
import com.gsvn.hrmservice.model.dto.request.StaffRequest;
import com.gsvn.hrmservice.model.dto.response.StaffResponse;
import com.gsvn.hrmservice.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staffs")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    public ApiResponse<StaffResponse> create(@RequestBody @Valid StaffCreateRequest request) {
        return new ApiResponse<>(staffService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<StaffResponse> update(@PathVariable Long id, @RequestBody @Valid StaffRequest request) {
        return new ApiResponse<>(staffService.update(id, request));
    }
    @PutMapping("/{id}/account")
    public ApiResponse<StaffResponse> initAccount(@PathVariable Long id) {
        return new ApiResponse<>(staffService.addAccountForStaff(id));
    }
    @GetMapping("/active")
    public ApiResponse<List<StaffResponse>> getAll() {
        return new ApiResponse<>(staffService.getActiveStaff());
    }

    @GetMapping("/{id}")
    public ApiResponse<StaffResponse> getById(@PathVariable Long id) {
        return new ApiResponse<>(staffService.getById(id));
    }
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadAvatar(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {
        return new ApiResponse<>(staffService.uploadStaffAvatar(id, file));
    }
    @GetMapping("/search")
    public ApiResponse<PageResponse<StaffResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer warehouseId,
            @RequestParam(required = false) Integer positionId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return new ApiResponse<>(staffService.searchStaffs(
                keyword,
                warehouseId,
                positionId,
                sortBy,
                direction,
                page,
                size
        ));
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        staffService.delete(id);
        return new ApiResponse<>();
    }
    @GetMapping("/profile/my-info")
    public ApiResponse<StaffResponse> getMyInfo() {
        return new ApiResponse<>(staffService.getMyInfo());
    }
    @PutMapping("/profile/my-info")
    public ApiResponse<StaffResponse> updateMyInfo(@RequestBody @Valid StaffRequest request) {
        return new ApiResponse<>(staffService.updateMyInfo(request));
    }
    @GetMapping("/internal/{id}")
    public ApiResponse<StaffResponse> getInternalById(@PathVariable Long id) {
        return new ApiResponse<>(staffService.getById(id));
    }
}