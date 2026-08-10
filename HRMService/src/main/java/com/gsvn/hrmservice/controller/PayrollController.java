package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.common.PageResponse;

import com.gsvn.hrmservice.common.util.DateUtils;

import com.gsvn.hrmservice.model.dto.request.PayrollApproveRequest;
import com.gsvn.hrmservice.model.dto.response.PayrollReportResponse;
import com.gsvn.hrmservice.model.dto.response.PayrollResponse;

import com.gsvn.hrmservice.service.PayrollService;
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

import java.util.List;


@RestController
@RequestMapping("/api/v1/payrolls")
@RequiredArgsConstructor
@Tag(name = "Payroll Management (Admin)", description = "Endpoints for HR/Finance administrators to calculate, approve, process payments, and generate payroll reports")
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("/init/{yearMonth}")
    @Operation(summary = "Initialize monthly payroll", description = "Triggers monthly payroll calculation for all employees for a specific period (Format: YYYY-MM).")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('payroll_init'))")
    public ApiResponse<Boolean> initPayroll(@Parameter(description = "Salary period in YYYY-MM format (e.g., 2026-05)") @PathVariable String yearMonth) {
        payrollService.initMonthlyPayroll(yearMonth);
        return new ApiResponse<>(true);
    }

    @GetMapping("/list/{yearMonth}")
    @Operation(summary = "Get payroll list by period", description = "Retrieves all payroll records for a specific period (Format: YYYY-MM).")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('payroll_read'))")
    public ApiResponse<List<PayrollResponse>> getPayrollList(@Parameter(description = "Salary period in YYYY-MM format (e.g., 2026-05)") @PathVariable String yearMonth) {
        return new ApiResponse<>(payrollService.getPayrollList(yearMonth));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get payroll detail", description = "Retrieves detailed breakdown of a specific payroll record by ID.")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('payroll_read'))")
    public ApiResponse<PayrollResponse> getDetail(@PathVariable Long id) {
        return new ApiResponse<>(payrollService.getPayrollDetail(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('payroll_permission'))")
    @Operation(summary = "Approve or reject payroll", description = "Processes manager approval or rejection for a specific payroll record.")
    public ApiResponse<Boolean> approvePayroll(
            @PathVariable Long id,
            @RequestBody @Valid PayrollApproveRequest request) {
        payrollService.processApproval(id, request);
        return new ApiResponse<>(true);
    }

    @PostMapping("/{id}/confirm-payment")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('payroll_permission'))")
    @Operation(summary = "Confirm payment", description = "Marks a specific payroll record as paid.")
    public ApiResponse<Boolean> confirmPayment(@PathVariable Long id) {
        payrollService.confirmPayment(id);
        return new ApiResponse<>(true);
    }

    @GetMapping("/{id}/print")
    @Operation(summary = "Export individual payslip PDF", description = "Generates and downloads the PDF payslip document for a specific payroll ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Payslip PDF generated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = @Schema(type = "string", format = "binary"))
    )
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('payroll_permission'))")
    public ResponseEntity<byte[]> print(@PathVariable Long id) {
        byte[] pdf = payrollService.exportPayrollPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=PhieuLuong.pdf")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(pdf);
    }

    @GetMapping("/search")
    @Operation(summary = "Search payroll records", description = "Retrieves a paginated list of payroll records filtered by keyword, period, and status.")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('payroll_read'))")
    public ApiResponse<PageResponse<PayrollResponse>> adminFilter(
            @Parameter(description = "Keyword to search by employee name or email") @RequestParam(required = false) String keyword,
            @Parameter(description = "Month filter (1-12)") @RequestParam(required = false) Integer month,
            @Parameter(description = "Year filter (e.g., 2026)") @RequestParam(required = false) Integer year,
            @Parameter(description = "Status filter (e.g., DRAFT, PENDING, APPROVED, PAID)") @RequestParam(required = false) String status,
            @Parameter(description = "Page index (1-based)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        String salaryPeriod = null;
        if (month != null) {
            salaryPeriod = DateUtils.formatSalaryPeriod(month, year);
        } else if (year != null) {
            salaryPeriod = String.valueOf(year);
        }
        return new ApiResponse<>(payrollService.searchPayrolls(keyword, salaryPeriod,status,page,size));
    }

    @GetMapping("/report")
    @Operation(summary = "Get payroll summary report", description = "Retrieves monthly or yearly aggregate payroll reports and statistics.")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('payroll_permission'))")
    public ApiResponse<PayrollReportResponse> getPayrollReport(
            @Parameter(description = "Month filter (1-12). If omitted, generates a yearly report.") @RequestParam(required = false) Integer month,
            @Parameter(description = "Year filter (e.g., 2026)") @RequestParam Integer year)
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

    @Operation(summary = "Export payroll summary report PDF", description = "Generates and downloads a PDF file for monthly or yearly aggregate payroll reports.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Payroll report PDF generated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = @Schema(type = "string", format = "binary"))
    )
    @GetMapping("/report/print")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('payroll_permission'))")
    public ResponseEntity<byte[]> printReport(  @Parameter(description = "Month filter (1-12). If omitted, generates a yearly report.") @RequestParam(required = false) Integer month,
                                                @Parameter(description = "Year filter (e.g., 2026)") @RequestParam Integer year) {
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