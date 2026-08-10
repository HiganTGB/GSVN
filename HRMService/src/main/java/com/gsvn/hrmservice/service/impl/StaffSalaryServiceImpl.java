package com.gsvn.hrmservice.service.impl;

import com.gsvn.hrmservice.converter.StaffSalaryConverter;
import com.gsvn.hrmservice.exc.AppException;
import com.gsvn.hrmservice.exc.ErrorCode;
import com.gsvn.hrmservice.mapper.StaffMapper;
import com.gsvn.hrmservice.mapper.StaffSalaryMapper;
import com.gsvn.hrmservice.model.dto.request.StaffSalaryRequest;
import com.gsvn.hrmservice.model.dto.response.StaffSalaryResponse;
import com.gsvn.hrmservice.model.entity.Staff;
import com.gsvn.hrmservice.model.entity.StaffSalary;
import com.gsvn.hrmservice.service.PositionService;
import com.gsvn.hrmservice.service.StaffSalaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class StaffSalaryServiceImpl implements StaffSalaryService {

    private final StaffSalaryMapper staffSalaryMapper;
    private final StaffMapper staffMapper;
    private final PositionService positionService;
    private final StaffSalaryConverter salaryConverter;


    @Override
    @Transactional
    public StaffSalaryResponse changeSalary(Long staffId, StaffSalaryRequest request) {
        Staff staff = staffMapper.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        var position=positionService.getById(request.getPositionId());
        if(position.getPositionId().equals(request.getPositionId())&&(staff.getBaseSalary().equals(request.getBaseSalary())))
        {   throw new AppException(ErrorCode.INVALID_REQUEST_BODY);}
        StaffSalary newSalary = salaryConverter.toEntity(request,staffId);
        newSalary.setPositionName(position.getPositionName());
        newSalary.setStaffId(staffId);
        staffSalaryMapper.insert(newSalary);
        staff.setBaseSalary(newSalary.getBaseSalary());
        staff.setPositionId(newSalary.getPositionId());
        staffMapper.update(staff);

        return salaryConverter.toResponse(newSalary);
    }

    @Override
    public StaffSalaryResponse getSalaryInfo(Long staffId) {
         StaffSalary salary= staffSalaryMapper.findSalaryByStaff(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
         return salaryConverter.toResponse(salary);
    }

    @Override
    public List<StaffSalaryResponse> getStaffSalaryHistory(Long staffId) {
        if (staffMapper.findById(staffId).isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        List<StaffSalary> entities = staffSalaryMapper.findHistoryByStaff(staffId);
        return salaryConverter.toResponseList(entities);
    }
}