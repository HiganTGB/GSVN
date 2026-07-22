package com.gsvn.shipmentservice.model.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConfirmDeliveringRequest {
    private String deliveryMethod;
    private String partnerProvinceCode;
    private String partnerDistrictCode;
    private String partnerWardCode;
    private BigDecimal actualShippingCost;
    private String trackingNumber;
}