package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.response.PayrollResponse;
import com.gsvn.hrmservice.service.PayrollService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/my-payroll")
@RequiredArgsConstructor
@Tag(name = "Employee Self-Service - Payroll", description = "Endpoints for employees to view their own payroll history, detailed payslips, and download PDF statements")
public class MyPayRollController {

    private final PayrollService payrollService;

    @Operation(summary = "Get personal payroll history", description = "Retrieves a paginated list of payroll records for the currently authenticated employee.")
    @GetMapping("/payroll/history")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<PageResponse<PayrollResponse>> getMyHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(payrollService.getMyPayrollHistory(page, size));
    }

    @Operation(summary = "Get personal payroll details", description = "Retrieves detailed information of a specific payroll record belonging to the authenticated employee.")
    @GetMapping("/payroll/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<PayrollResponse> getMyDetail(@PathVariable Long id) {
        return new ApiResponse<>(payrollService.getMyPayrollDetail(id));
    }

    @Operation(summary = "Download monthly payslip PDF", description = "Generates and downloads the PDF payslip document for a specific payroll record.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Payslip PDF generated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary"))
    )
    @GetMapping("/payroll/{id}/pdf")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<byte[]> downloadMonthlyPdf(@PathVariable Long id) {
        byte[] pdfContent = payrollService.exportMyMonthlyPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Payslip_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

}