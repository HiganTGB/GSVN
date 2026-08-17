package com.gsvn.hrmservice.converter;

import com.gsvn.hrmservice.common.util.DateUtils;
import com.gsvn.hrmservice.model.dto.request.StaffRequest;
import com.gsvn.hrmservice.model.dto.response.BranchResponse;
import com.gsvn.hrmservice.model.dto.response.PositionResponse;
import com.gsvn.hrmservice.model.dto.response.StaffResponse;
import com.gsvn.hrmservice.model.entity.Staff;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class StaffConverter {

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
                .branchId(request.getBranchId())
                .build();
    }

    public StaffResponse toResponse(Staff entity, PositionResponse position, BranchResponse branch) {
        if (entity == null) return null;

        if (position == null) {
            position = PositionResponse.builder().positionName("Unknown").build();
        }
        if (branch == null) {
            branch = BranchResponse.builder().branchName("Unknown").build();
        }

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

                .branchId(entity.getBranchId())
                .branchName(branch.getBranchId() != null && branch.getBranchId().equals(entity.getBranchId()) ? branch.getBranchName() : "Unknown")

                .baseSalary(entity.getBaseSalary())
                .positionId(entity.getPositionId())
                .positionName(position.getPositionId() != null && position.getPositionId().equals(entity.getPositionId()) ? position.getPositionName() : "Unknown")

                .isActive(entity.getIsActive())
                .createdAt(DateUtils.toString(entity.getCreatedAt(), DateUtils.DATE_ONLY_FORMAT))
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
        entity.setBranchId(request.getBranchId());
    }

    public List<StaffResponse> toResponseList(List<Staff> entities, List<PositionResponse> allPositions, List<BranchResponse> allBranches) {
        if (entities == null) return null;

        Map<Integer, PositionResponse> positionMap = allPositions.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        PositionResponse::getPositionId,
                        p -> p,
                        (existing, replacement) -> existing
                ));

        Map<Integer, BranchResponse> branchMap = allBranches.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        BranchResponse::getBranchId,
                        b -> b,
                        (existing, replacement) -> existing
                ));

        return entities.stream()
                .map(entity -> {
                    PositionResponse pos = entity.getPositionId() != null ? positionMap.get(entity.getPositionId()) : null;
                    BranchResponse bra = entity.getBranchId() != null ? branchMap.get(entity.getBranchId()) : null;
                    return toResponse(entity, pos, bra);
                })
                .collect(Collectors.toList());
    }
}