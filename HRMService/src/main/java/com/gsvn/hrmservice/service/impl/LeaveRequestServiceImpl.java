package com.gsvn.hrmservice.service.impl;

import com.gsvn.hrmservice.client.UserServiceFeignClient;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.common.util.WordGeneratorUtil;
import com.gsvn.hrmservice.converter.LeaveRequestConverter;
import com.gsvn.hrmservice.exc.AppException;
import com.gsvn.hrmservice.exc.ErrorCode;
import com.gsvn.hrmservice.mapper.LeaveRequestMapper;
import com.gsvn.hrmservice.mapper.StaffMapper;
import com.gsvn.hrmservice.model.dto.request.LeaveRequestRequest;
import com.gsvn.hrmservice.model.dto.request.LeaveStatusApproveRequest;
import com.gsvn.hrmservice.model.dto.response.LeaveRequestResponse;
import com.gsvn.hrmservice.model.entity.LeaveRequest;
import com.gsvn.hrmservice.model.entity.Staff;
import com.gsvn.hrmservice.model.enums.Status;
import com.gsvn.hrmservice.model.enums.LeaveType;
import com.gsvn.hrmservice.service.AuthenticationService;
import com.gsvn.hrmservice.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class    LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestMapper leaveMapper;
    private final StaffMapper staffMapper;
    private final WordGeneratorUtil wordGeneratorUtil;
    private final LeaveRequestConverter converter;
    private final UserServiceFeignClient userServiceClient;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional
    public LeaveRequestResponse create(LeaveRequestRequest request) {
        var currentId = authenticationService.getStaffIdFromToken();
        LeaveRequest entity = converter.toEntity(request);

        Staff s = staffMapper.findById(currentId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        entity.setStaffId(s.getStaffId());
        entity.setStaffName(s.getFullName());


        entity.setStatus(Status.PENDING);

        leaveMapper.insert(entity);
        return converter.toResponse(entity);
    }

    @Override
    @Transactional
    public LeaveRequestResponse update(Long id, LeaveRequestRequest request) {
        LeaveRequest existing = leaveMapper.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));


        if (Status.PENDING != existing.getStatus()) {
            throw new AppException(ErrorCode.NOT_ALLOW);
        }

        LeaveRequest updateData = converter.toEntity(request);
        updateData.setId(id);
        updateData.setStatus(Status.PENDING);

        leaveMapper.update(updateData);
        return converter.toResponse(updateData);
    }


    @Override
    @Transactional
    public void delete(Long id) {
        LeaveRequest lr = leaveMapper.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));


        if (!Status.PENDING.equals(lr.getStatus())) {
            throw new AppException(ErrorCode.NOT_ALLOW);
        }
        leaveMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void approveRequest(Long requestId, LeaveStatusApproveRequest request) {
        LeaveRequest lr = leaveMapper.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));

        Staff manager = staffMapper.findById(authenticationService.getStaffIdFromToken())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        leaveMapper.updateStatus(requestId, request.getStatus(), manager.getStaffId(), manager.getFullName(),request.getNote());

        if (Status.APPROVED.equals(request.getStatus()) && LeaveType.RESIGNATION.equals(lr.getLeaveType())) {
            processStaffResignation(lr.getStaffId());
        }
    }
    private void processStaffResignation(Long staffId) {
        Staff staff = staffMapper.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        staffMapper.updateActive(staffId, false);

        if (staff.getUserId() != null) {
            userServiceClient.lockUser(staff.getUserId());
            log.info("Staff {} has been deactivated and user account locked.", staffId);
        }
    }

    @Override
    public byte[] exportPdf(Long id) {
        LeaveRequest lr = leaveMapper.findById(id).orElseThrow();

        if (!Status.APPROVED.equals(lr.getStatus())) {
            throw new AppException(ErrorCode.NOT_ALLOW);
        }
        byte[] content = wordGeneratorUtil.createLeaveRequestDoc(lr);
        return wordGeneratorUtil.convertDocxToPdf(content);
    }
    @Override
    public PageResponse<LeaveRequestResponse> getMyHistory(int page, int size) {

        var staffId = authenticationService.getStaffIdFromToken();

        int offset = (page - 1) * size;

        List<LeaveRequest> data = leaveMapper.findMyHistory(staffId, size, offset);
        int total = leaveMapper.countMyHistory(staffId);

        return PageResponse.of(converter.toResponseDTOList(data), total,page,size);
    }
    @Override
    public LeaveRequestResponse getById(Long id) {
        return leaveMapper.findById(id)
                .map(converter::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
    }

    @Override
    public PageResponse<LeaveRequestResponse> search(Status status, Integer month, Integer year, int page, int size) {
        page = Math.max(1, page);
        int offset = (page - 1) * size;
        List<LeaveRequest> data = leaveMapper.search(status, month, year, size, offset);
        int total = leaveMapper.countSearch(status, month, year);
        return PageResponse.of(converter.toResponseDTOList(data), total, page,size);
    }

}