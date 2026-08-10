package com.gsvn.shipmentservice.model.dto.request;

import com.gsvn.shipmentservice.model.entity.DeliveryMethod;
import lombok.Data;

@Data
public class ShipmentProcessRequest {
    private String partnerProvinceCode;
    private String partnerDistrictCode;
    private String partnerWardCode;
    private DeliveryMethod deliveryMethod;
}
