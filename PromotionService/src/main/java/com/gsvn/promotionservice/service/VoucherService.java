package com.gsvn.promotionservice.service;

import com.gsvn.promotionservice.converter.VoucherConverter;
import com.gsvn.promotionservice.exc.AppException;
import com.gsvn.promotionservice.exc.ErrorCode;
import com.gsvn.promotionservice.mapper.VoucherMapper;
import com.gsvn.promotionservice.model.dto.request.VoucherRequest;
import com.gsvn.promotionservice.common.PageResponse;
import com.gsvn.promotionservice.model.dto.response.VoucherResponse;
import com.gsvn.promotionservice.model.entity.Voucher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherMapper voucherMapper;
    private final VoucherConverter converter;
    private final AuthenticationService authenticationService;

    @Transactional
    public VoucherResponse create(VoucherRequest request) {
        Voucher entity = converter.toEntity(request);
        voucherMapper.insert(entity);
        return converter.toResponse(entity);
    }
    @Transactional
    public VoucherResponse getById(Integer id) {
        Voucher entity = voucherMapper.findById(id);
        if (entity == null) throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        return converter.toResponse(entity);
    }
    @Transactional
    public VoucherResponse getByCode(String code) {
        Voucher entity = voucherMapper.findByCode(code);
        if (entity == null) throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        return converter.toResponse(entity);
    }

    @Transactional
    public VoucherResponse update(Integer id, VoucherRequest request) {
        Voucher entity = voucherMapper.findById(id);
        if (entity == null) throw new AppException(ErrorCode.ITEM_NOT_EXISTED);

        entity.setName(request.getName());
        entity.setDiscountType(request.getDiscountType());
        entity.setDiscountValue(request.getDiscountValue());
        entity.setMaxDiscountAmount(request.getMaxDiscountAmount());
        entity.setMinOrderValue(request.getMinOrderValue());
        entity.setUsageLimit(request.getUsageLimit());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setIsActive(request.getIsActive());

        voucherMapper.update(entity);
        return converter.toResponse(entity);
    }

    @Transactional
    public boolean detele(Integer id)
    {
        Voucher entity = voucherMapper.findById(id);
        if (entity == null) throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        voucherMapper.delete(id);
        return true;
    }


    public PageResponse<VoucherResponse> getPage(String keyword,  int page, int size,String sortBy,String direction) {

        int offset = (page - 1) * size;

        String sortField = switch (sortBy != null ? sortBy : "") {
            case "name" -> "name";
            default -> "created_at";
        };

        String sortOrder = "asc".equalsIgnoreCase(direction) ? "ASC" : "DESC";


        List<Voucher> entities = voucherMapper.findSearch(keyword,size,offset,sortField,sortOrder);
        long totalElements = voucherMapper.countSearch(keyword);

        List<VoucherResponse> content = entities.stream()
                .map(converter::toResponse)
                .collect(Collectors.toList());
        return PageResponse.of(content,totalElements,page,size);
    }
}