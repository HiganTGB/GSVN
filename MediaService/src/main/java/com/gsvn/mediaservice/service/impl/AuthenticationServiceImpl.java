package com.gsvn.mediaservice.service.impl;


import com.gsvn.mediaservice.client.AuthServiceFeignClient;
import com.gsvn.mediaservice.common.ApiResponse;
import com.gsvn.mediaservice.common.IntrospectRequest;
import com.gsvn.mediaservice.common.IntrospectResponse;
import com.gsvn.mediaservice.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    AuthServiceFeignClient authServiceFeignClient;
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) {
        try {
            ApiResponse<IntrospectResponse> response = authServiceFeignClient.authenticate(introspectRequest);
            return response.result();
        } catch (Exception e) {
            log.info(e.getMessage());
            return new IntrospectResponse(false);
        }
    }
}
