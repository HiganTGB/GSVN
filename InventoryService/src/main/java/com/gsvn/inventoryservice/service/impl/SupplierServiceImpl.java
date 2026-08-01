package com.gsvn.inventoryservice.service.impl;

import com.gsvn.inventoryservice.converter.SupplierConverter;
import com.gsvn.inventoryservice.exc.AppException;
import com.gsvn.inventoryservice.exc.ErrorCode;
import com.gsvn.inventoryservice.mapper.SupplierMapper;
import com.gsvn.inventoryservice.model.dto.request.SupplierRequest;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.response.SupplierResponse;
import com.gsvn.inventoryservice.model.entity.Supplier;
import com.gsvn.inventoryservice.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierMapper supplierMapper;
    private final SupplierConverter supplierConverter;

    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        Supplier entity = supplierConverter.toEntity(request);
        supplierMapper.insert(entity);
        return supplierConverter.toResponse(entity);
    }

    @Transactional
    public SupplierResponse update(Integer id, SupplierRequest request) {
        Supplier existingEntity = supplierMapper.findById(id).orElseThrow(()->new AppException(ErrorCode.ITEM_NOT_EXISTED));

        supplierConverter.updateEntity(existingEntity, request);
        existingEntity.setId(id);

        supplierMapper.update(existingEntity);

        return supplierConverter.toResponse(existingEntity);
    }

    public SupplierResponse getById(Integer id) {
        Supplier entity = supplierMapper.findById(id).orElseThrow(()->new AppException(ErrorCode.ITEM_NOT_EXISTED));
        return supplierConverter.toResponse(entity);
    }

    public PageResponse<SupplierResponse> getPage(String keyword, Boolean isActive,
                                                  String sortBy,
                                                  String direction,
                                                  int page,
                                                  int size
    ) {

        int offset = (page - 1) * size;

        String sortField = switch (sortBy != null ? sortBy : "") {
            case "name" -> "name";
            default -> "created_at";
        };

        String sortOrder = "asc".equalsIgnoreCase(direction) ? "ASC" : "DESC";
        List<Supplier> entities = supplierMapper.findPage(keyword, isActive,sortField,sortOrder, size, offset);
        long totalElements = supplierMapper.countSearch(keyword, isActive);

        List<SupplierResponse> content = supplierConverter.toResponseList(entities);

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<SupplierResponse>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .numberOfElements(content.size())
                .first(page <= 1)
                .last(page >= totalPages || totalPages == 0)
                .build();
    }

    @Transactional
    public void delete(Integer id) {
        Supplier entity = supplierMapper.findById(id).orElseThrow(()->new AppException(ErrorCode.ITEM_NOT_EXISTED));
        supplierMapper.delete(id);
    }
    public List<SupplierResponse> getAll() {
        return supplierConverter.toResponseList(supplierMapper.findAll()) ;
    }
}