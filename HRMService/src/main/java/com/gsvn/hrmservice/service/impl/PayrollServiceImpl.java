package com.gsvn.hrmservice.service.impl;

import com.gsvn.hrmservice.common.PageResponse;

import com.gsvn.hrmservice.common.util.WordGeneratorUtil;
import com.gsvn.hrmservice.converter.PayrollConverter;
import com.gsvn.hrmservice.exc.AppException;
import com.gsvn.hrmservice.exc.ErrorCode;
import com.gsvn.hrmservice.mapper.*;
import com.gsvn.hrmservice.model.dto.PayrollReportItem;
import com.gsvn.hrmservice.model.dto.request.PayrollApproveRequest;
import com.gsvn.hrmservice.model.dto.response.PayrollReportResponse;
import com.gsvn.hrmservice.model.dto.response.PayrollResponse;


import com.gsvn.hrmservice.model.entity.Payroll;
import com.gsvn.hrmservice.model.entity.Position;
import com.gsvn.hrmservice.model.entity.Staff;

import com.gsvn.hrmservice.model.enums.PayrollStatus;
import com.gsvn.hrmservice.service.AuthenticationService;
import com.gsvn.hrmservice.service.PayrollService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class PayrollServiceImpl implements PayrollService {

    private final PayrollMapper payrollMapper;
    private final LeaveRequestMapper leaveMapper;
    private final StaffMapper staffMapper;
    private final WordGeneratorUtil wordGeneratorUtil;
    private final PositionMapper positionMapper;
    private final AuthenticationService authenticationService;
    private final PayrollConverter payrollConverter;
    @Override
    @Transactional
    public void initMonthlyPayroll(String yearMonth) {

        YearMonth ym = YearMonth.parse(yearMonth);
        YearMonth currentMonth = YearMonth.now();
        if (ym.isAfter(currentMonth)) {
            throw new AppException(ErrorCode.NOT_ALLOW);
        }
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();

        List<Staff> activeStaffs = staffMapper.findListByActive(true);
        if (activeStaffs.isEmpty()) return;

        Map<Integer, String> positionMap = positionMapper.findAll().stream()
                .collect(Collectors.toMap(Position::getPositionId, Position::getPositionName));

        for (Staff staff : activeStaffs) {
            Double unpaidDays = leaveMapper.sumUnpaidDaysInMonth(
                    staff.getStaffId(),
                    firstDay,
                    lastDay
            );
            if (unpaidDays == null) unpaidDays = 0.0;


            double standardDays = 26.0;
            double calculatedWorkingDays = Math.max(0, standardDays - unpaidDays);

            BigDecimal baseSalary = staff.getBaseSalary();
            BigDecimal dailyRate = baseSalary.divide(new BigDecimal("26"), 4, RoundingMode.HALF_UP);


            BigDecimal finalSalary = dailyRate.multiply(new BigDecimal(calculatedWorkingDays))
                    .setScale(0, RoundingMode.HALF_UP);

            String currentPositionName = positionMap.getOrDefault(staff.getPositionId(), "N/A");

            Payroll payroll = Payroll.builder()
                    .staffId(staff.getStaffId())
                    .staffName(staff.getFullName())
                    .salaryPeriod(yearMonth)
                    .positionId(staff.getPositionId())
                    .positionName(currentPositionName)
                    .baseSalary(baseSalary)
                    .workingDays(calculatedWorkingDays)
                    .totalBonus(BigDecimal.ZERO)
                    .totalDeduction(BigDecimal.ZERO)
                    .finalSalary(finalSalary)
                    .status(PayrollStatus.PENDING)
                    .note(String.format("Lương tính từ 26 ngày công, trừ %.1f ngày nghỉ không lương", unpaidDays))
                    .build();

            payrollMapper.insert(payroll);
        }
    }
    public List<PayrollResponse> getPayrollList(String yearMonth){
        var list= payrollMapper.getPayrollListByYearMonth(yearMonth);
        return payrollConverter.toResponseDTOList(list);
    }
    @Override
    public PageResponse<PayrollResponse> searchPayrolls(String keyword,String salaryPeriod,String status, int page, int size) {
        page = Math.max(1, page);
        int offset = (page - 1) * size;
        List<Payroll> list = payrollMapper.findAdvanced(keyword,salaryPeriod,status,offset, size);
        var total = payrollMapper.countAdvanced(keyword,salaryPeriod,status);
        return PageResponse.of(payrollConverter.toResponseDTOList(list), total, page, size);
    }
    @Override
    public PayrollResponse getPayrollDetail(Long payrollId) {
        var payroll= payrollMapper.findById(payrollId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
        return payrollConverter.toResponse(payroll);
    }

    @Override
    @Transactional
    public void processApproval(Long payrollId, PayrollApproveRequest req) {
        Payroll payroll = payrollMapper.findById(payrollId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
        var managerStaffId = authenticationService.getStaffIdFromToken();
        var managerStaff = staffMapper.findById(managerStaffId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (!PayrollStatus.PENDING.equals(payroll.getStatus())) {
            throw new AppException(ErrorCode.NOT_ALLOW);
        }

        if (Boolean.TRUE.equals(req.getIsAccepted())) {
            BigDecimal dailyRate = payroll.getBaseSalary().divide(new BigDecimal("26"), 4, RoundingMode.HALF_UP);
            BigDecimal baseFinal = dailyRate.multiply(BigDecimal.valueOf(payroll.getWorkingDays()));
            BigDecimal totalFinal = baseFinal.add(req.getTotalBonus())
                    .subtract(req.getTotalDeduction())
                    .setScale(0, RoundingMode.HALF_UP);

            payroll.setTotalBonus(req.getTotalBonus());
            payroll.setTotalDeduction(req.getTotalDeduction());
            payroll.setFinalSalary(totalFinal);
            payroll.setStatus(PayrollStatus.APPROVED);
        } else {
            payroll.setStatus(PayrollStatus.CANCELLED);
        }

        payroll.setNote(req.getNote());
        payroll.setApprovedBy(managerStaffId);
        payroll.setApprovedName(managerStaff.getFullName());
        payrollMapper.updateApproval(payroll);
    }


    @Override
    public byte[] exportPayrollPdf(Long payrollId) {
        Payroll payroll = payrollMapper.findById(payrollId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
        return exportPdf(payroll);
    }


    @Override
    @Transactional
    public void confirmPayment(Long payrollId) {
        payrollMapper.findById(payrollId).orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
        payrollMapper.markAsPaid(payrollId);
    }


    @Override
    public PageResponse<PayrollResponse> getMyPayrollHistory(int page, int size) {
        var staffId = authenticationService.getStaffIdFromToken();
        int offset = (page - 1) * size;

        List<Payroll> data = payrollMapper.findHistoryByStaff(staffId, offset, size);
        int totalElements = payrollMapper.countHistoryByStaff(staffId);

        return PageResponse.of(payrollConverter.toResponseDTOList(data), totalElements, size, page);
    }

    @Override
    public PayrollResponse getMyPayrollDetail(Long payrollId) {
        var staffId = authenticationService.getStaffIdFromToken();
        Payroll payroll = payrollMapper.findById(payrollId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));

        if (!payroll.getStaffId().equals(staffId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return payrollConverter.toResponse(payroll);
    }

    @Override
    public byte[] exportMyMonthlyPdf(Long payrollId) {
        Payroll payroll = payrollMapper.findById(payrollId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));

        if (PayrollStatus.PENDING.equals(payroll.getStatus())) {
            throw new AppException(ErrorCode.NOT_ALLOW);
        }
        return exportPdf(payroll);
    }

    @Override
    public PayrollReportResponse getPayrollReport(String period, boolean isYearly) {
        String queryPeriod = period;
        String reportTitle = "";

        if (isYearly) {
            queryPeriod = period.substring(0, 4);
            reportTitle = "Báo cáo quyết toán năm " + queryPeriod;
        } else {
            reportTitle = "Bảng lương chi tiết tháng " + period;
        }

        String reportType = isYearly ? "YEARLY" : "MONTHLY";
        List<PayrollReportItem> items = payrollMapper.getPayrollReport(queryPeriod, reportType);
        PayrollReportItem summary = payrollMapper.getPayrollReportTotal(queryPeriod, reportType);
        if (items.isEmpty()) {
            return PayrollReportResponse.builder()
                    .reportPeriod(isYearly ? "Năm " + period : "Tháng " + period)
                    .items(new ArrayList<>())
                    .summary(new PayrollReportItem())
                    .build();
        }
        return PayrollReportResponse.builder()
                .reportPeriod(isYearly ? "Báo cáo quyết toán năm " + period : "Bảng lương chi tiết tháng " + period)
                .items(items)
                .summary(summary)
                .build();
    }
    public byte[] exportReportPdf(String period, boolean isYearly)
    {
        byte[] wordBytes = wordGeneratorUtil.createPayrollReportDoc(getPayrollReport(period,isYearly));
        return wordGeneratorUtil.convertDocxToPdf(wordBytes);
    }
    private byte[] exportPdf(Payroll payroll) {
        if (PayrollStatus.PENDING.equals(payroll.getStatus())) {
            throw new AppException(ErrorCode.NOT_ALLOW);
        }


        byte[] wordBytes = wordGeneratorUtil.createPayrollDoc(payroll);
        return wordGeneratorUtil.convertDocxToPdf(wordBytes);
    }




}