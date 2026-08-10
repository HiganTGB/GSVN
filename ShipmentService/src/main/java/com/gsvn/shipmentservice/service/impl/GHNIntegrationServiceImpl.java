package com.gsvn.shipmentservice.service.impl;

import com.gsvn.shipmentservice.model.dto.GHNOrderRequest;
import com.gsvn.shipmentservice.service.GHNIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GHNIntegrationServiceImpl implements GHNIntegrationService {

    @Value("${app.ship.ghn}")
    private String ghnUrl;
    private final RestTemplate restTemplate;

    public Map<String, Object> createOrderToGHN(GHNOrderRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Token", request.getToken());
            headers.set("ShopId", String.valueOf(request.getShopId()));

            HttpEntity<GHNOrderRequest> entity = new HttpEntity<>(request, headers);

            log.info("Creating GHN order for client order code: {}", request.getClientOrderCode());

            ResponseEntity<Map> response = restTemplate.postForEntity(ghnUrl, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                if (body.get("code") != null && body.get("code").toString().equals("200")) {
                    log.info("GHN order created successfully. Tracking: {}",
                            ((Map)body.get("data")).get("order_code"));
                    return body;
                } else {
                    log.error("GHN returned a business logic error: {}", body.get("message"));
                    throw new RuntimeException("GHN Error: " + body.get("message"));
                }
            }

            throw new RuntimeException("GHN API connection error: " + response.getStatusCode());

        } catch (Exception e) {
            log.error("Critical error while calling GHN: {}", e.getMessage());
            throw new RuntimeException("Unable to connect to the shipping partner GHN");
        }
    }
}