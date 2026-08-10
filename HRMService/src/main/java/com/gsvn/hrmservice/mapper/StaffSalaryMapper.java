package com.gsvn.hrmservice.mapper;

import com.gsvn.hrmservice.model.entity.StaffSalary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StaffSalaryMapper {

    Optional<StaffSalary> findSalaryByStaff(@Param("staffId") Long staffId);
    List<StaffSalary> findHistoryByStaff(@Param("staffId") Long staffId);
    int insert(StaffSalary staffSalary);
}