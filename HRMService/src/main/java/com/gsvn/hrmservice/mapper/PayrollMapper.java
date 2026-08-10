package com.gsvn.hrmservice.mapper;

import com.gsvn.hrmservice.model.dto.PayrollReportItem;
import com.gsvn.hrmservice.model.entity.Payroll;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PayrollMapper {

    void insert(Payroll payroll);

    Optional<Payroll> findById(Long id);

    List<Payroll> findAdvanced(@Param("keyword") String keyword,
                               @Param("salaryPeriod") String salaryPeriod,
                               @Param("status") String status,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    int countAdvanced(@Param("keyword") String keyword,
                      @Param("salaryPeriod") String salaryPeriod,
                      @Param("status") String status);

    List<Payroll> getPayrollListByYearMonth(@Param("yearMonth") String yearMonth);


    void updateApproval(Payroll payroll);

    void markAsPaid(Long id);

    List<Payroll> findHistoryByStaff(@Param("staffId") Long staffId,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    int countHistoryByStaff(Long staffId);


    List<PayrollReportItem> getPayrollReport(@Param("period") String period,
                                             @Param("type") String type);

    PayrollReportItem getPayrollReportTotal(@Param("period") String period,
                                            @Param("type") String type);
}