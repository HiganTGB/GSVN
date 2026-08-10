package com.gsvn.hrmservice.converter;

import com.gsvn.hrmservice.common.IBaseConverter;
import com.gsvn.hrmservice.common.util.DateUtils;
import com.gsvn.hrmservice.model.dto.request.StaffRequest;
import com.gsvn.hrmservice.model.dto.response.PositionResponse;
import com.gsvn.hrmservice.model.dto.response.StaffResponse;
import com.gsvn.hrmservice.model.entity.Position;
import com.gsvn.hrmservice.model.entity.Staff;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class StaffConverter  {


    public Staff toEntity(StaffRequest request) {
        if (request == null) return null;

        return Staff.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .dob(request.getDob())
                .gender(request.getGender())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .identityCard(request.getIdentityCard())
                .warehouseId(request.getWarehouseId())
                .build();
    }


    public StaffResponse toResponse(Staff entity, PositionResponse position) {
        if (entity == null) return null;
        if (position==null) position=PositionResponse.builder().positionName("Unknown").build();
        return StaffResponse.builder()
                .staffId(entity.getStaffId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .dob(entity.getDob())
                .gender(entity.getGender())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .identityCard(entity.getIdentityCard())
                .avatarUrl(entity.getAvatarUrl())
                .warehouseId(entity.getWarehouseId())
                .baseSalary(entity.getBaseSalary())
                .positionId(entity.getPositionId())
                .positionName((position.getPositionId().equals(entity.getPositionId())? position.getPositionName():"Unknown" ))
                .isActive(entity.getIsActive())
                .createdAt(DateUtils.toString(entity.getCreatedAt(),DateUtils.DATE_ONLY_FORMAT))
                .updatedAt(DateUtils.toString(entity.getUpdatedAt()))
                .build();
    }
    public void mapRequestToEntity(StaffRequest request, Staff entity) {
        if (request == null || entity == null) return;

        entity.setFullName(request.getFullName());
        entity.setEmail(request.getEmail());
        entity.setDob(request.getDob());
        entity.setGender(request.getGender());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setAddress(request.getAddress());
        entity.setIdentityCard(request.getIdentityCard());
        entity.setWarehouseId(request.getWarehouseId());
    }
    public List<StaffResponse> toResponseList(List<Staff> entities, List<PositionResponse> allPositions) {
        if (entities == null) return null;


        Map<Integer, PositionResponse> positionMap = allPositions.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        PositionResponse::getPositionId,
                        p -> p,
                        (existing, replacement) -> existing // Giữ lại bản ghi đầu tiên nếu trùng ID
                ));

        return entities.stream()
                .map(entity -> {
                    PositionResponse pos = positionMap.get(entity.getPositionId());
                    return toResponse(entity, pos);
                })
                .collect(Collectors.toList());
    }
}