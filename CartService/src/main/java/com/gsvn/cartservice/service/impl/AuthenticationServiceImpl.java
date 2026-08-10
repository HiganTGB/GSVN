package com.gsvn.cartservice.service.impl;



import com.gsvn.cartservice.client.AuthServiceFeignClient;
import com.gsvn.cartservice.common.ApiResponse;
import com.gsvn.cartservice.config.CustomAuthenticationToken;
import com.gsvn.cartservice.model.internal.IntrospectRequest;
import com.gsvn.cartservice.model.internal.IntrospectResponse;
import com.gsvn.cartservice.service.AuthenticationService;
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
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthServiceFeignClient authServiceFeignClient;
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) {
        ApiResponse<IntrospectResponse> response = authServiceFeignClient.authenticate(introspectRequest);
        return response.result();
    }
    public Long getCustomerIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof CustomAuthenticationToken customerAuth) {
            return customerAuth.getCustomerId();
        }
        return null;
    }

}
