package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.request.LeaveRequestRequest;
import com.gsvn.hrmservice.model.dto.request.LeaveStatusApproveRequest;
import com.gsvn.hrmservice.model.dto.response.LeaveRequestResponse;
import com.gsvn.hrmservice.model.enums.Status;
import com.gsvn.hrmservice.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leave-requests")
@RequiredArgsConstructor
@Tag(name = "Leave Request Management", description = "Endpoints for creating, approving, filtering, and exporting leave requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveService;

    @PostMapping
    @Operation(summary = "Submit leave request", description = "Submits a new leave request for the authenticated employee.")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<LeaveRequestResponse> submit(@RequestBody @Valid LeaveRequestRequest req) {
        return new ApiResponse<>(leaveService.create(req));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update leave request", description = "Updates an existing pending leave request by ID.")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<LeaveRequestResponse> update(@PathVariable long id, @RequestBody @Valid LeaveRequestRequest req) {
        return new ApiResponse<>(leaveService.update(id, req));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete leave request", description = "Deletes a leave request by ID.")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<Void> delete(@PathVariable long id) {
        leaveService.delete(id);
        return new ApiResponse<>(null);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get leave request details", description = "Retrieves detailed information of a specific leave request.")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('leave_read'))")
    public ApiResponse<LeaveRequestResponse> getDetail(@PathVariable long id) {
        return new ApiResponse<>(leaveService.getById(id));
    }


    @GetMapping("/my-history")
    @Operation(summary = "Get personal leave history", description = "Retrieves a paginated list of leave requests submitted by the current authenticated user.")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<PageResponse<LeaveRequestResponse>> myHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(leaveService.getMyHistory(page, size));
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter leave requests", description = "Admin/Manager endpoint to filter leave requests by status, month, and year with pagination.")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('leave_read'))")
    public ApiResponse<PageResponse<LeaveRequestResponse>> adminFilter(
            @Parameter(description = "Filter by leave status (e.g., PENDING, APPROVED, REJECTED)") @RequestParam(required = false) Status status,
            @Parameter(description = "Filter by month (1-12)") @RequestParam(required = false) Integer month,
            @Parameter(description = "Filter by year (e.g., 2026)") @RequestParam(required = false) Integer year,
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(leaveService.search(status, month, year, page, size));
    }


    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve or reject leave request", description = "Approves or rejects a submitted leave request with optional remarks.")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('leave_permission'))")
    public ApiResponse<Void> approve(@PathVariable long id, @RequestBody @Valid LeaveStatusApproveRequest status) {
        leaveService.approveRequest(id, status);
        return new ApiResponse<>(null);
    }


    @GetMapping("/{id}/print")
    @Operation(summary = "Export leave request PDF", description = "Generates and downloads a PDF document for a specific leave request.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "PDF document generated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary"))
    )
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<byte[]> print(@PathVariable long id) {
        byte[] pdf = leaveService.exportPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Don_Nghi_Phep_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}