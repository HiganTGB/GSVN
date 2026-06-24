package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.response.PayrollResponse;
import com.gsvn.hrmservice.service.PayrollService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/my-payroll")
@RequiredArgsConstructor
public class MyPayRollController {

    private final PayrollService payrollService;


    @GetMapping("/payroll/history")
    public ApiResponse<PageResponse<PayrollResponse>> getMyHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(payrollService.getMyPayrollHistory(page, size));
    }


    @GetMapping("/payroll/{id}")
    public ApiResponse<PayrollResponse> getMyDetail(@PathVariable Long id) {
        return new ApiResponse<>(payrollService.getMyPayrollDetail(id));
    }


    @GetMapping("/payroll/{id}/pdf")
    public ResponseEntity<byte[]> downloadMonthlyPdf(@PathVariable Long id) {
        byte[] pdfContent = payrollService.exportMyMonthlyPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Payslip_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

}