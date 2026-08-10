package com.gsvn.shipmentservice.service;

import com.gsvn.shipmentservice.model.dto.GHNOrderRequest;

import java.util.Map;


public interface GHNIntegrationService {

    public Map<String, Object> createOrderToGHN(GHNOrderRequest request);
}