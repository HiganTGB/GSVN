package com.gsvn.shipmentservice.client;

import com.gsvn.shipmentservice.common.ApiResponse;
import com.gsvn.shipmentservice.config.InternalFeignConfig;
import com.gsvn.shipmentservice.model.dto.internal.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service", path = "/api/v1/payments",configuration = InternalFeignConfig.class)
public interface PaymentClient {

    @PostMapping("/internal/confirm-cod")
   ApiResponse<Boolean> confirmCod(
            @RequestBody PaymentRequest request);
}