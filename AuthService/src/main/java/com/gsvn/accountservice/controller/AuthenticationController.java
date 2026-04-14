package com.gsvn.accountservice.controller;

import java.text.ParseException;

import com.gsvn.accountservice.common.ApiResponse;
import com.gsvn.accountservice.model.dto.request.*;
import com.gsvn.accountservice.model.dto.response.AuthenticationResponse;

import com.gsvn.accountservice.model.internal.IntrospectRequest;
import com.gsvn.accountservice.model.internal.IntrospectResponse;
import com.gsvn.accountservice.service.AuthenticationService;
import com.gsvn.accountservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    UserService userService;
    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return new ApiResponse<>(result);
    }
    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return new ApiResponse<>(result);
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return new ApiResponse<>();
    }
    @PostMapping("/internal/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return new ApiResponse<>(result);
    }
    @PostMapping("/register")
    ApiResponse<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request)  {
        userService.registerCustomer(request);
        var result = authenticationService.authenticate(new AuthenticationRequest(request.getEmail(),request.getPassword()));
        return new ApiResponse<>(result);
    }

}