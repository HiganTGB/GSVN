package com.gsvn.accountservice.controller;

import java.text.ParseException;

import com.gsvn.accountservice.common.ApiResponse;
import com.gsvn.accountservice.model.dto.request.*;
import com.gsvn.accountservice.model.dto.response.AuthenticationResponse;

import com.gsvn.accountservice.model.internal.IntrospectRequest;
import com.gsvn.accountservice.model.internal.IntrospectResponse;
import com.gsvn.accountservice.service.AuthenticationService;
import com.gsvn.accountservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Endpoints for user authentication, registration, token management, and password recovery")
public class AuthenticationController {
    AuthenticationService authenticationService;
    UserService userService;
    @PostMapping("/token")
    @Operation(summary = "User login", description = "Authenticates user credentials and returns access and refresh tokens.")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return new ApiResponse<>(result);
    }
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Generates a new access token using a valid refresh token.")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return new ApiResponse<>(result);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidates the active session and revokes provided tokens.")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return new ApiResponse<>();
    }
    @PostMapping("/internal/introspect")
    @Operation(summary = "Introspect token (Internal)", description = "Verifies token validity")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return new ApiResponse<>(result);
    }
    @PostMapping("/register")
    @Operation(summary = "Register customer", description = "Creates a new customer account")
    ApiResponse<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request)  {
        userService.registerCustomer(request);
        var result = authenticationService.authenticate(new AuthenticationRequest(request.getEmail(),request.getPassword()));
        return new ApiResponse<>(result);
    }
    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Sends a password reset to the email address.")
    ApiResponse<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request)  {
         authenticationService.requestResetPassword(request.email());

        return new ApiResponse<>(request.email());
    }
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Resets user password using a valid token.")
    public ApiResponse<Boolean> resetPassword(@RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request.getToken(), request.getNewPassword());
        return new ApiResponse<>(true);
    }

}