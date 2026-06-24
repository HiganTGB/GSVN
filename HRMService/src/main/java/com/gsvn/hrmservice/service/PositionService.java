package com.gsvn.hrmservice.service;


import com.gsvn.hrmservice.common.IBaseService;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.request.PositionRequest;
import com.gsvn.hrmservice.model.dto.response.PositionResponse;
import com.gsvn.hrmservice.model.entity.Position;
import java.util.List;

public interface PositionService extends IBaseService<PositionRequest, PositionResponse,Integer> {

    List<PositionResponse> getAllPositions();

    PageResponse<PositionResponse> getPage(String keyword, String sortBy, String direction, int page, int size);

    ;
}