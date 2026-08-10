package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.request.WarehouseRequest;
import com.gsvn.inventoryservice.model.dto.response.WarehouseResponse;
import com.gsvn.inventoryservice.model.internal.WarehousePartnerRequest;
import com.gsvn.inventoryservice.model.internal.WarehousePartnerResponse;

import java.util.List;

public interface WarehouseService {
    WarehouseResponse create(WarehouseRequest request);
    WarehouseResponse update(Integer id, WarehouseRequest request);
    WarehouseResponse getById(Integer id);
    WarehouseResponse getByCode(String code);
    PageResponse<WarehouseResponse> getPage(String keyword, String sortBy,
                                            String direction,
                                            int page,
                                            int size
    );
    void delete(Integer id);
    List<WarehouseResponse> getAll();
    String getDecryptedToken(Integer warehouseId, String partnerName);
    List<WarehousePartnerResponse> getPartnersByWarehouseId(Integer warehouseId);
    void deletePartner(Integer warehouseId, String partnerName);
    WarehousePartnerResponse savePartnerToken(WarehousePartnerRequest request, Integer warehouseId);
}
