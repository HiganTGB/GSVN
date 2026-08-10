package com.gsvn.accountservice.client.fallback;

import com.gsvn.accountservice.client.NotificationClient;
import com.gsvn.accountservice.common.ApiResponse;
import com.gsvn.accountservice.exc.AppException;
import com.gsvn.accountservice.exc.ErrorCode;
import com.gsvn.accountservice.model.internal.PasswordResetRequest;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationClientFallbackFactory implements FallbackFactory<NotificationClient> {

    @Override
    public NotificationClient create(Throwable cause) {
        return new NotificationClient() {
            @Override
            public ApiResponse<String> sendResetPasswordEmail(PasswordResetRequest request) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Notification Service failed to send reset password email to: {}. Cause: {}",
                        request.getEmail(), cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}