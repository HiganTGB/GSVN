package com.gsvn.promotionservice.service;

import com.gsvn.promotionservice.model.dto.request.VoucherRequest;
import com.gsvn.promotionservice.common.PageResponse;
import com.gsvn.promotionservice.model.dto.response.VoucherResponse;
public interface VoucherService {


    VoucherResponse create(VoucherRequest request);
    VoucherResponse getById(Integer id);
    VoucherResponse getByCode(String code) ;
    VoucherResponse update(Integer id, VoucherRequest request) ;
    boolean detele(Integer id);
    PageResponse<VoucherResponse> getPage(String keyword,  int page, int size,String sortBy,String direction);
}