package com.gsvn.hrmservice.mapper;



import com.gsvn.hrmservice.common.IBaseMapper;
import com.gsvn.hrmservice.model.entity.LeaveRequest;
import com.gsvn.hrmservice.model.entity.Staff;
import com.gsvn.hrmservice.model.entity.StaffSalary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StaffMapper extends IBaseMapper<Staff, Long> {
    List<Staff> findAdvanced(@Param("keyword") String keyword,
                             @Param("warehouseId") Integer warehouseId,
                             @Param("positionId") Integer positionId,
                             @Param("sortField") String sortField,
                             @Param("sortOrder") String sortOrder,
                             @Param("limit") int limit,
                             @Param("offset") int offset);

    int countAdvanced(@Param("keyword") String keyword,
                      @Param("warehouseId") Integer warehouseId, @Param("positionId") Integer positionId);
    List<Staff> findListByActive(@Param("isActive") boolean isActive);
    Optional<Staff> findByUserId(@Param("userId") String userId);
    int updateActive(@Param("staffId") Long staffId, @Param("isActive") Boolean isActive);
    boolean existsByEmail(@Param("email") String email, @Param("staffId") Long staffId);

    boolean existsByIdentityCard(@Param("identityCard") String identityCard, @Param("staffId") Long staffId);

}