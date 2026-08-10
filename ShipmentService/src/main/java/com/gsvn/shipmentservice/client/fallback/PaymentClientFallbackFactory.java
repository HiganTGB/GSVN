package com.gsvn.shipmentservice.client.fallback;

import com.gsvn.shipmentservice.client.PaymentClient;
import com.gsvn.shipmentservice.common.ApiResponse;
import com.gsvn.shipmentservice.exc.AppException;
import com.gsvn.shipmentservice.exc.ErrorCode;
import com.gsvn.shipmentservice.model.dto.internal.PaymentRequest;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentClientFallbackFactory implements FallbackFactory<PaymentClient> {

    @Override
    public PaymentClient create(Throwable cause) {
        return new PaymentClient() {

            @Override
            public ApiResponse<Boolean> confirmCod(PaymentRequest request) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Payment Service confirmCod failed for orderCode/id: {}. Cause: {}",
                        request != null ? request.getOrderCode() : "N/A", cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}