package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.request.OutboundRequest;
import com.gsvn.inventoryservice.model.dto.response.OutboundResponse;

public interface OutboundService {
    OutboundResponse processOutbound(OutboundRequest request);
    PageResponse<OutboundResponse> getOutboundPage(Integer warehouseId, String type, String keyword, int page, int size);
    OutboundResponse getOutboundDetail(Long id);
    byte[] exportOutboundDetail(Long id);
}
