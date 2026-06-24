package com.gsvn.hrmservice.service;

import com.gsvn.hrmservice.common.IBaseService;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.request.LeaveRequestRequest;
import com.gsvn.hrmservice.model.dto.request.LeaveStatusApproveRequest;
import com.gsvn.hrmservice.model.dto.response.LeaveRequestResponse;
import com.gsvn.hrmservice.model.enums.Status;


public interface LeaveRequestService extends IBaseService<LeaveRequestRequest, LeaveRequestResponse, Long> {


    void approveRequest(Long requestId, LeaveStatusApproveRequest status);


    PageResponse<LeaveRequestResponse> getMyHistory(int page, int size);


    PageResponse<LeaveRequestResponse> search(Status status, Integer month, Integer year, int page, int size);


    byte[] exportPdf(Long id);
}