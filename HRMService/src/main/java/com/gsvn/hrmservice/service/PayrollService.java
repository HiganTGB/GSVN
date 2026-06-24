package com.gsvn.hrmservice.service;

import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.request.PayrollApproveRequest;
import com.gsvn.hrmservice.model.dto.response.PayrollReportResponse;
import com.gsvn.hrmservice.model.dto.response.PayrollResponse;



import java.util.List;

public interface PayrollService {
    // For admin
    void initMonthlyPayroll(String yearMonth);

    List<PayrollResponse> getPayrollList(String yearMonth);

    PageResponse<PayrollResponse> searchPayrolls(String keyword, String salaryPeriod, String status, int page, int size);

    PayrollResponse getPayrollDetail(Long payrollId);

    void processApproval(Long payrollId, PayrollApproveRequest req);

    byte[] exportPayrollPdf(Long payrollId);

    void confirmPayment(Long payrollId);

    // Personal
    PageResponse<PayrollResponse> getMyPayrollHistory(int page, int size);

    PayrollResponse getMyPayrollDetail(Long payrollId);

    byte[] exportMyMonthlyPdf(Long payrollId);

    PayrollReportResponse getPayrollReport(String period, boolean isYearly);
    public byte[] exportReportPdf(String period, boolean isYearly);

}