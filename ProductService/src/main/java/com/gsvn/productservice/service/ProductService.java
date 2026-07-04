package com.gsvn.productservice.service;


import com.gsvn.productservice.common.PageResponse;

import com.gsvn.productservice.model.dto.request.ProductBasicUpdateRequest;
import com.gsvn.productservice.model.dto.request.ProductPreOrderUpdateRequest;
import com.gsvn.productservice.model.dto.request.ProductCreateRequest;
import com.gsvn.productservice.model.dto.response.*;
import com.gsvn.productservice.model.dto.response.ProductBasicResponse;
import com.gsvn.productservice.model.dto.response.ProductPreCampaignResponse;

import com.gsvn.productservice.model.entity.SaleStatus;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service
public interface ProductService {


    public Integer createProduct(ProductCreateRequest request);

    public ProductBasicResponse updateBasic(ProductBasicUpdateRequest request,Integer productId);


    public void deleteProduct(Integer productId);


    public ProductPreCampaignResponse updatePreCampaign(ProductPreOrderUpdateRequest request, Integer productId);

    public Boolean changeActivePreCampaign(Integer productId,Boolean isActive);


    public ProductPreCampaignResponse deletePreCampaign(Integer productId);


    public String uploadMainImage(Integer productId, MultipartFile file) ;


    public List<String> addGalleryImage(Integer productId, MultipartFile file);


    public void deleteGalleryImage(Integer productId, String objectName);


    public PageResponse<ProductBasicResponse> getPage(String keyword,
                                                 Integer brandId,
                                                 Integer categoryId,
                                                 SaleStatus saleStatus,
                                                 Boolean isActive,
                                                 String sortBy,
                                                 String direction,
                                                 int page,
                                                 int size);

    public ProductBasicResponse getBasic(Integer productId);

    public ProductPreCampaignResponse getPreResponse(Integer productId);

    public List<PreHistoryResponse> getPreHistoryResponse(Integer productId);
    public Map<String,String> getGallery(Integer productId);

}