package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.common.PageResponse;

import com.gsvn.hrmservice.common.util.DateUtils;

import com.gsvn.hrmservice.model.dto.request.PayrollApproveRequest;
import com.gsvn.hrmservice.model.dto.response.PayrollReportResponse;
import com.gsvn.hrmservice.model.dto.response.PayrollResponse;

import com.gsvn.hrmservice.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/payrolls")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;
    @PostMapping("/init/{yearMonth}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('payroll_init')")
    public ApiResponse<Boolean> initPayroll(@PathVariable String yearMonth) {
        payrollService.initMonthlyPayroll(yearMonth);
        return new ApiResponse<>(true);
    }

    @GetMapping("/list/{yearMonth}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('payroll_read')")
    public ApiResponse<List<PayrollResponse>> getPayrollList(@PathVariable String yearMonth) {
        return new ApiResponse<>(payrollService.getPayrollList(yearMonth));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('payroll_read')")
    public ApiResponse<PayrollResponse> getDetail(@PathVariable Long id) {
        return new ApiResponse<>(payrollService.getPayrollDetail(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('all') or hasAuthority('payroll_permission')")
    public ApiResponse<Boolean> approvePayroll(
            @PathVariable Long id,
            @RequestBody @Valid PayrollApproveRequest request) {
        payrollService.processApproval(id, request);
        return new ApiResponse<>(true);
    }

    @PostMapping("/{id}/confirm-payment")
    @PreAuthorize("hasAuthority('all') or hasAuthority('payroll_permission')")
    public ApiResponse<Boolean> confirmPayment(@PathVariable Long id) {
        payrollService.confirmPayment(id);
        return new ApiResponse<>(true);
    }
    @GetMapping("/{id}/print")
    @PreAuthorize("hasAuthority('all') or hasAuthority('payroll_permission')")
    public ResponseEntity<byte[]> print(@PathVariable Long id) {
        byte[] pdf = payrollService.exportPayrollPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=PhieuLuong.pdf")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(pdf);
    }
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('all') or hasAuthority('payroll_read')")
    public ApiResponse<PageResponse<PayrollResponse>> adminFilter(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        String salaryPeriod = null;
        if (month != null) {
            salaryPeriod = DateUtils.formatSalaryPeriod(month, year);
        } else if (year != null) {
            salaryPeriod = String.valueOf(year);
        }
        return new ApiResponse<>(payrollService.searchPayrolls(keyword, salaryPeriod,status,page,size));
    }
    @GetMapping("/report")
    @PreAuthorize("hasAuthority('all') or hasAuthority('payroll_permission')")
    public ApiResponse<PayrollReportResponse> getPayrollReport(
            @RequestParam(required = false) Integer month,
            @RequestParam Integer year)
    {
        boolean isYearly = (month == null);
        String period;
        if (isYearly) {
            period = String.valueOf(year);
        } else {
            period = String.format("%d-%02d", year, month);
        }
        PayrollReportResponse report = payrollService.getPayrollReport(period, isYearly);
        return new ApiResponse<>(report);
    }
    @GetMapping("/report/print")
    @PreAuthorize("hasAuthority('all') or hasAuthority('payroll_permission')")
    public ResponseEntity<byte[]> printReport(    @RequestParam(required = false) Integer month,
                                                  @RequestParam Integer year) {
        boolean isYearly = (month == null);
        String period;
        if (isYearly) {
            period = String.valueOf(year);
        } else {
            period = String.format("%d-%02d", year, month);
        }
        var pdf=payrollService.exportReportPdf(period,isYearly);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=PhieuLuong.pdf")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(pdf);
    }
}