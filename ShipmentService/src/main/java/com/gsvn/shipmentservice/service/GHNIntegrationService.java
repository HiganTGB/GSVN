package com.gsvn.shipmentservice.service;

import com.gsvn.shipmentservice.model.dto.GHNOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GHNIntegrationService {

    private final RestTemplate restTemplate;
    private static final String GHN_URL = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/create";

    public Map<String, Object> createOrderToGHN(GHNOrderRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Token", request.getToken());
            headers.set("ShopId", String.valueOf(request.getShopId()));

            HttpEntity<GHNOrderRequest> entity = new HttpEntity<>(request, headers);

            log.info("==> Đang tạo đơn hàng GHN cho mã đơn: {}", request.getClientOrderCode());

            ResponseEntity<Map> response = restTemplate.postForEntity(GHN_URL, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                if (body.get("code") != null && body.get("code").toString().equals("200")) {
                    log.info("==> Tạo đơn GHN thành công. Tracking: {}",
                            ((Map)body.get("data")).get("order_code"));
                    return body;
                } else {
                    log.error("==> GHN trả về lỗi nghiệp vụ: {}", body.get("message"));
                    throw new RuntimeException("GHN Error: " + body.get("message"));
                }
            }

            throw new RuntimeException("Lỗi kết nối API GHN: " + response.getStatusCode());

        } catch (Exception e) {
            log.error("==> Lỗi nghiêm trọng khi gọi GHN: {}", e.getMessage());
            throw new RuntimeException("Không thể kết nối với đối tác vận chuyển GHN");
        }
    }
}