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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staffs")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('staff_create')")
    public ApiResponse<StaffResponse> create(@RequestBody @Valid StaffCreateRequest request) {
        return new ApiResponse<>(staffService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('staff_update')")
    public ApiResponse<StaffResponse> update(@PathVariable Long id, @RequestBody @Valid StaffRequest request) {
        return new ApiResponse<>(staffService.update(id, request));
    }
    @PutMapping("/{id}/account")
    @PreAuthorize("hasAuthority('all') or hasAuthority('staff_create')")
    public ApiResponse<StaffResponse> initAccount(@PathVariable Long id) {
        return new ApiResponse<>(staffService.addAccountForStaff(id));
    }
    @GetMapping("/active")
    @PreAuthorize("hasAuthority('all') or hasAuthority('staff_update')")
    public ApiResponse<List<StaffResponse>> getAll() {
        return new ApiResponse<>(staffService.getActiveStaff());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('staff_read')")
    public ApiResponse<StaffResponse> getById(@PathVariable Long id) {
        return new ApiResponse<>(staffService.getById(id));
    }
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('all') or hasAuthority('staff_update')")
    public ApiResponse<String> uploadAvatar(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {
        return new ApiResponse<>(staffService.uploadStaffAvatar(id, file));
    }
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('all') or hasAuthority('staff_read')")
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
    @PreAuthorize("hasAuthority('all') or hasAuthority('staff_delete')")
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