package com.gsvn.cartservice.service;



import com.gsvn.cartservice.client.AuthServiceFeignClient;
import com.gsvn.cartservice.common.ApiResponse;
import com.gsvn.cartservice.config.CustomAuthenticationToken;
import com.gsvn.cartservice.model.internal.IntrospectRequest;
import com.gsvn.cartservice.model.internal.IntrospectResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticationService {
    private final AuthServiceFeignClient authServiceFeignClient;
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) {
        try {
            ApiResponse<IntrospectResponse> response = authServiceFeignClient.authenticate(introspectRequest);
            return response.result();
        } catch (Exception e) {
            log.info(e.getMessage());
            return new IntrospectResponse(false);
        }
    }
    public Long getCustomerIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof CustomAuthenticationToken customerAuth) {
            return customerAuth.getCustomerId();
        }
        return null;
    }

}
