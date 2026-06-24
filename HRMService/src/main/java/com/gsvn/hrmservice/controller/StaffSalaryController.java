package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.model.dto.request.StaffSalaryRequest;
import com.gsvn.hrmservice.model.dto.response.StaffSalaryResponse;
import com.gsvn.hrmservice.service.AuthenticationService;
import com.gsvn.hrmservice.service.StaffSalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff-salaries")
@RequiredArgsConstructor
public class StaffSalaryController {

    private final StaffSalaryService salaryService;
    private final AuthenticationService authenticationService;

    @GetMapping("/my-salary")
    public ApiResponse<StaffSalaryResponse> getCurrentSalary() {
        return new ApiResponse<>(salaryService.getSalaryInfo(authenticationService.getStaffIdFromToken()));
    }
    @PostMapping("/{staffId}")
    public ApiResponse<StaffSalaryResponse> changeSalary(@PathVariable Long staffId, @Valid @RequestBody StaffSalaryRequest request) {
        return new ApiResponse<>(salaryService.changeSalary(staffId, request));
    }

    @GetMapping("/{staffId}/history")
    public ApiResponse<List<StaffSalaryResponse>> getStaffSalaryHistory(@PathVariable Long staffId) {
        return new ApiResponse<>(salaryService.getStaffSalaryHistory(staffId));
    }
}