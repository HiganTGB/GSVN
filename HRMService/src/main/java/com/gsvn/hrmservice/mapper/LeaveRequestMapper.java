package com.gsvn.hrmservice.mapper;

import com.gsvn.hrmservice.common.IBaseMapper;
import com.gsvn.hrmservice.model.entity.LeaveRequest;
import com.gsvn.hrmservice.model.enums.Status;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LeaveRequestMapper extends IBaseMapper<LeaveRequest,Long>{

    List<LeaveRequest> search(@Param("status") Status status,
                              @Param("month") Integer month,
                              @Param("year") Integer year,
                              @Param("size") int size,
                              @Param("offset") int offset);

    int countSearch(@Param("status") Status status,
                    @Param("month") Integer month,
                    @Param("year") Integer year);

    List<LeaveRequest> findMyHistory(@Param("staffId") Long staffId,
                                     @Param("size") int size,
                                     @Param("offset") int offset);

    int countMyHistory(@Param("staffId") Long staffId);

    int updateStatus(@Param("id") Long id,
                     @Param("status") Status status,
                     @Param("approvedBy") Long approvedBy,
                     @Param("approvedName") String approvedName,
                     @Param("note") String note);

    Double sumUnpaidDaysInMonth(@Param("staffId") Long staffId,
                                @Param("firstDay") LocalDate firstDay,
                                @Param("lastDay") LocalDate lastDay);
}