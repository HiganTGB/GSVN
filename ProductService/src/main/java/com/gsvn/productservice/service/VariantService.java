package com.gsvn.productservice.service;


import com.gsvn.productservice.model.dto.request.ProductVariantSyncRequest;
import com.gsvn.productservice.model.dto.response.VariantResponse;

import org.springframework.stereotype.Service;


import java.util.List;



@Service
public interface VariantService {

    void syncVariants(Integer productId, List<ProductVariantSyncRequest.VariantUpdateDto> requestVariants);


    public List<VariantResponse> getVariantByProduct(Integer productId);


}