package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.model.dto.request.StaffSalaryRequest;
import com.gsvn.hrmservice.model.dto.response.StaffSalaryResponse;
import com.gsvn.hrmservice.service.AuthenticationService;
import com.gsvn.hrmservice.service.StaffSalaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff-salaries")
@RequiredArgsConstructor
@Tag(name = "Staff Salary Management", description = "Endpoints for managing employee base salaries, salary adjustments, and personal salary lookups")
public class StaffSalaryController {

    private final StaffSalaryService salaryService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Get current base salary", description = "Self-service endpoint to retrieve current base salary details for the authenticated employee.")
    @GetMapping("/my-salary")
    public ApiResponse<StaffSalaryResponse> getCurrentSalary() {
        return new ApiResponse<>(salaryService.getSalaryInfo(authenticationService.getStaffIdFromToken()));
    }

    @Operation(summary = "Change or update staff base salary", description = "Updates or sets a new base salary configuration for a specific staff member.")
    @PostMapping("/{staffId}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('staff_update_salary')")
    public ApiResponse<StaffSalaryResponse> changeSalary(
            @Parameter(description = "ID of the staff member") @PathVariable Long staffId,
            @Valid @RequestBody StaffSalaryRequest request) {
        return new ApiResponse<>(salaryService.changeSalary(staffId, request));
    }

    @Operation(summary = "Get staff salary history", description = "Retrieves the historical record of base salary changes for a specific staff member.")
    @GetMapping("/{staffId}/history")
    @PreAuthorize("hasAuthority('all') or hasAuthority('staff_read_salary')")
    public ApiResponse<List<StaffSalaryResponse>> getStaffSalaryHistory(
            @Parameter(description = "ID of the staff member") @PathVariable Long staffId) {
        return new ApiResponse<>(salaryService.getStaffSalaryHistory(staffId));
    }
}