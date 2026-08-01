package com.gsvn.shipmentservice.service;


import com.gsvn.shipmentservice.model.dto.request.WarehousePartnerRequest;
import com.gsvn.shipmentservice.model.dto.response.WarehousePartnerResponse;

import java.util.List;

public interface WarehousePartnerService {

    WarehousePartnerResponse savePartnerToken(WarehousePartnerRequest request, String warehouseCode);
    String getDecryptedToken(String warehouseCode, String partnerName);
    List<WarehousePartnerResponse> getPartnersByWarehouseId(String warehouseCode);
    void deletePartner(String warehouseCode, String partnerName);
}
