package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.request.StaffCreateRequest;
import com.gsvn.hrmservice.model.dto.request.StaffRequest;
import com.gsvn.hrmservice.model.dto.response.StaffResponse;
import com.gsvn.hrmservice.service.StaffService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Staff Management", description = "Endpoints for managing staff profiles, account initialization, avatar upload, and employee self-service")
public class StaffController {

    private final StaffService staffService;

    @Operation(summary = "Create staff", description = "Creates a new staff profile record.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('staff_create'))")
    public ApiResponse<StaffResponse> create(@RequestBody @Valid StaffCreateRequest request) {
        return new ApiResponse<>(staffService.create(request));
    }

    @Operation(summary = "Update staff", description = "Updates an existing staff profile by ID.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('staff_update'))")
    public ApiResponse<StaffResponse> update(
            @Parameter(description = "ID of the staff member") @PathVariable Long id,
            @RequestBody @Valid StaffRequest request) {
        return new ApiResponse<>(staffService.update(id, request));
    }

    @Operation(summary = "Initialize user account for staff", description = "Provisions a system user account for an existing staff profile.")
    @PutMapping("/{id}/account")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('staff_create'))")
    public ApiResponse<StaffResponse> initAccount(
            @Parameter(description = "ID of the staff member") @PathVariable Long id) {
        return new ApiResponse<>(staffService.addAccountForStaff(id));
    }

    @Operation(summary = "Get active staff list", description = "Retrieves a list of all currently active staff members.")
    @GetMapping("/active")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('staff_update'))")
    public ApiResponse<List<StaffResponse>> getAll() {
        return new ApiResponse<>(staffService.getActiveStaff());
    }

    @Operation(summary = "Get staff by ID", description = "Retrieves detailed profile information for a specific staff member.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('staff_read'))")
    public ApiResponse<StaffResponse> getById(
            @Parameter(description = "ID of the staff member") @PathVariable Long id) {
        return new ApiResponse<>(staffService.getById(id));
    }

    @Operation(
            summary = "Upload staff avatar",
            description = "Uploads a profile picture for a staff member using multipart form-data and returns the uploaded image URL."
    )
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('staff_update'))")
    public ApiResponse<String> uploadAvatar(
            @Parameter(description = "ID of the staff member") @PathVariable Long id,
            @Parameter(
                    description = "Image file to upload (JPEG, PNG)",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart("file") MultipartFile file) {
        return new ApiResponse<>(staffService.uploadStaffAvatar(id, file));
    }

    @Operation(summary = "Search staff members", description = "Retrieves a paginated list of staff members with optional filtering by keyword, warehouse, and position.")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('staff_read'))")
    public ApiResponse<PageResponse<StaffResponse>> search(
            @Parameter(description = "Keyword to search by staff name, code, or phone") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by warehouse ID") @RequestParam(required = false) Integer warehouseId,
            @Parameter(description = "Filter by position ID") @RequestParam(required = false) Integer positionId,
            @Parameter(description = "Field name to sort results by") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sorting direction: 'asc' or 'desc'") @RequestParam(defaultValue = "desc") String direction,
            @Parameter(description = "Page number (1-based index)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {

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

    @Operation(summary = "Delete staff profile", description = "Deletes or deactivates a staff member profile by ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('staff_delete'))")
    public ApiResponse<Void> delete(
            @Parameter(description = "ID of the staff member to delete") @PathVariable Long id) {
        staffService.delete(id);
        return new ApiResponse<>();
    }

    @Operation(summary = "Get own staff profile", description = "Self-service endpoint to retrieve staff profile details for the currently authenticated employee.")
    @GetMapping("/profile/my-info")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<StaffResponse> getMyInfo() {
        return new ApiResponse<>(staffService.getMyInfo());
    }

    @Operation(summary = "Update own staff profile", description = "Self-service endpoint to update staff profile details for the currently authenticated employee.")
    @PutMapping("/profile/my-info")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<StaffResponse> updateMyInfo(@RequestBody @Valid StaffRequest request) {
        return new ApiResponse<>(staffService.updateMyInfo(request));
    }

    @Operation(summary = "Get staff profile by ID (Internal)", description = "Internal endpoint for inter-service communication to retrieve staff details.")
    @GetMapping("/internal/{id}")
    public ApiResponse<StaffResponse> getInternalById(
            @Parameter(description = "ID of the staff member") @PathVariable Long id) {
        return new ApiResponse<>(staffService.getById(id));
    }
}