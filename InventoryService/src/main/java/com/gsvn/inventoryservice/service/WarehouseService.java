package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.client.WarehousePartnerClient;
import com.gsvn.inventoryservice.common.utils.TokenEncryptionUtil;
import com.gsvn.inventoryservice.converter.WarehouseConverter;
import com.gsvn.inventoryservice.converter.WarehousePartnerConverter;
import com.gsvn.inventoryservice.exc.AppException;
import com.gsvn.inventoryservice.exc.ErrorCode;
import com.gsvn.inventoryservice.mapper.WarehouseMapper;
import com.gsvn.inventoryservice.model.internal.WarehousePartnerRequest;
import com.gsvn.inventoryservice.model.dto.request.WarehouseRequest;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.internal.WarehousePartnerResponse;
import com.gsvn.inventoryservice.model.dto.response.WarehouseResponse;
import com.gsvn.inventoryservice.model.entity.Warehouse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseMapper warehouseMapper;
    private final WarehouseConverter warehouseConverter;
    private final WarehousePartnerClient partnerClient;
    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
        Warehouse entity = warehouseConverter.toEntity(request);
        warehouseMapper.insert(entity);
        return warehouseConverter.toResponse(entity);
    }

    @Transactional
    public WarehouseResponse update(Integer id, WarehouseRequest request) {
        Warehouse existingEntity = warehouseMapper.findById(id);
        if (existingEntity == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }

        warehouseConverter.updateEntity(existingEntity, request);
        existingEntity.setId(id);

        warehouseMapper.update(existingEntity);

        return warehouseConverter.toResponse(existingEntity);
    }

    public WarehouseResponse getById(Integer id) {
        Warehouse entity = warehouseMapper.findById(id);
        if (entity == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
        return warehouseConverter.toResponse(entity);
    }
    public WarehouseResponse getByCode(String code) {
        Warehouse entity = warehouseMapper.findByCode(code);
        if (entity == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
        return warehouseConverter.toResponse(entity);
    }

    public PageResponse<WarehouseResponse> getPage(String keyword, String sortBy,
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
        List<Warehouse> entities = warehouseMapper.findAllPaged(keyword,sortField,sortOrder, size, offset);
        long totalElements = warehouseMapper.countAll(keyword);

        List<WarehouseResponse> content = warehouseConverter.toResponseList(entities);

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<WarehouseResponse>builder()
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
        Warehouse existing = warehouseMapper.findById(id);
        if (existing == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
    }
    public List<WarehouseResponse> getAll() {
      return warehouseConverter.toResponseList(warehouseMapper.findAll()) ;
    }
    @Transactional
    public WarehousePartnerResponse savePartnerToken(WarehousePartnerRequest request, Integer warehouseId) {

        Warehouse existingWarehouse = warehouseMapper.findById(warehouseId);
        if (existingWarehouse == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
        return partnerClient.savePartner(existingWarehouse.getCode(),request).result();
    }
    public String getDecryptedToken(Integer warehouseId, String partnerName) {
        Warehouse existingWarehouse = warehouseMapper.findById(warehouseId);
        return partnerClient.getDecryptedToken(existingWarehouse.getCode(),partnerName).result();
    }
    public List<WarehousePartnerResponse> getPartnersByWarehouseId(Integer warehouseId) {
        Warehouse existingWarehouse = warehouseMapper.findById(warehouseId);
        return partnerClient.getPartners(existingWarehouse.getCode()).result();
    }
    @Transactional
    public void deletePartner(Integer warehouseId, String partnerName) {
        Warehouse existingWarehouse = warehouseMapper.findById(warehouseId);
        partnerClient.deletePartner(existingWarehouse.getCode(), partnerName);
    }


}