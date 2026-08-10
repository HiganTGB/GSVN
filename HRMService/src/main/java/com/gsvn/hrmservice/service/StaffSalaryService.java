package com.gsvn.hrmservice.service;

import com.gsvn.hrmservice.model.dto.request.StaffSalaryRequest;
import com.gsvn.hrmservice.model.dto.response.StaffSalaryResponse;

import java.util.List;


public interface StaffSalaryService {

    StaffSalaryResponse changeSalary(Long staffId,StaffSalaryRequest request);
    StaffSalaryResponse getSalaryInfo(Long staffId);
    List<StaffSalaryResponse> getStaffSalaryHistory(Long staffId);

}