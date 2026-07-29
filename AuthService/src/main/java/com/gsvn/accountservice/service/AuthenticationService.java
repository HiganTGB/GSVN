package com.gsvn.accountservice.service;

import com.gsvn.accountservice.model.dto.request.AuthenticationRequest;
import com.gsvn.accountservice.model.dto.request.LogoutRequest;
import com.gsvn.accountservice.model.dto.request.RefreshRequest;
import com.gsvn.accountservice.model.dto.response.AuthenticationResponse;
import com.gsvn.accountservice.model.internal.IntrospectRequest;
import com.gsvn.accountservice.model.internal.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {

    IntrospectResponse introspect(IntrospectRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;

    void logout(LogoutRequest request);

    String generateResetPasswordToken(String email);

    String verifyResetPasswordToken(String token) throws JOSEException, ParseException;

    void requestResetPassword(String email);

    void resetPassword(String token, String newPassword);
}