package com.gsvn.productservice.service;

import com.gsvn.productservice.model.dto.request.BrandRequest;
import com.gsvn.productservice.model.dto.response.BrandResponse;
import com.gsvn.productservice.common.PageResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface BrandService {



    PageResponse<BrandResponse> getPage(String keyword,
                                               String sortBy,
                                               String direction,
                                               int page,
                                               int size
    ) ;
    List<BrandResponse> getList() ;


    BrandResponse getById(Integer id) ;


    BrandResponse create(BrandRequest request);

    BrandResponse update(Integer id, BrandRequest request) ;

    void delete(Integer id);
}