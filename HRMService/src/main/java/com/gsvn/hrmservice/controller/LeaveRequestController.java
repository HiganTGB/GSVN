package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.request.LeaveRequestRequest;
import com.gsvn.hrmservice.model.dto.request.LeaveStatusApproveRequest;
import com.gsvn.hrmservice.model.dto.response.LeaveRequestResponse;
import com.gsvn.hrmservice.model.enums.Status;
import com.gsvn.hrmservice.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveService;

    @PostMapping
    public ApiResponse<LeaveRequestResponse> submit(@RequestBody @Valid LeaveRequestRequest req) {
        return new ApiResponse<>(leaveService.create(req));
    }


    @PutMapping("/{id}")
    public ApiResponse<LeaveRequestResponse> update(@PathVariable long id, @RequestBody @Valid LeaveRequestRequest req) {
        return new ApiResponse<>(leaveService.update(id, req));
    }


    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable long id) {
        leaveService.delete(id);
        return new ApiResponse<>(null);
    }


    @GetMapping("/{id}")
    public ApiResponse<LeaveRequestResponse> getDetail(@PathVariable long id) {
        return new ApiResponse<>(leaveService.getById(id));
    }


    @GetMapping("/my-history")
    public ApiResponse<PageResponse<LeaveRequestResponse>> myHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(leaveService.getMyHistory(page, size));
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<LeaveRequestResponse>> adminFilter(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(leaveService.search(status, month, year, page, size));
    }


    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable long id, @RequestBody @Valid LeaveStatusApproveRequest status) {
        leaveService.approveRequest(id, status);
        return new ApiResponse<>(null);
    }


    @GetMapping("/{id}/print")
    public ResponseEntity<byte[]> print(@PathVariable long id) {
        byte[] pdf = leaveService.exportPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Don_Nghi_Phep_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}