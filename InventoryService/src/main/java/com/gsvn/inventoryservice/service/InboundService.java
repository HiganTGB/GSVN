package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.request.InboundRequest;
import com.gsvn.inventoryservice.model.dto.response.InboundResponse;

public interface InboundService {
    InboundResponse processInbound(InboundRequest request);
    PageResponse<InboundResponse> getInboundPage(Integer warehouseId, Integer supplierId, String type, String keyword, int page, int size);
    InboundResponse getInboundDetail(Long id);
    byte[] exportInboundDetail(Long id);
}
