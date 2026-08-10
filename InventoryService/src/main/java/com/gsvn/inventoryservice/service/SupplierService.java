package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.request.SupplierRequest;
import com.gsvn.inventoryservice.model.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {
    SupplierResponse create(SupplierRequest request);
    SupplierResponse update(Integer id, SupplierRequest request);
    SupplierResponse getById(Integer id);
    PageResponse<SupplierResponse> getPage(String keyword, Boolean isActive,
                                           String sortBy,
                                           String direction,
                                           int page,
                                           int size
    );
    void delete(Integer id);
    List<SupplierResponse> getAll();
}
