package com.gsvn.accountservice.config;

import com.gsvn.accountservice.exc.AppException;
import com.gsvn.accountservice.exc.ErrorCode;
import com.gsvn.accountservice.model.dto.response.AuthenticationResponse;
import com.gsvn.accountservice.service.AuthenticationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;

    @Value("${app.oauth2.redirect-uri:http://localhost:3000/oauth2/redirect}")
    private String targetRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        OAuth2User oauth2User = oauthToken.getPrincipal();

        String providerName;
        String providerUserId;
        String email;
        String fullName;

        if ("google".equalsIgnoreCase(registrationId)) {
            providerName = "GOOGLE";
            providerUserId = oauth2User.getAttribute("sub");
            email = oauth2User.getAttribute("email");
            fullName = oauth2User.getAttribute("name");
        } else {
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY);
        }

        if (providerUserId == null || email == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        AuthenticationResponse authResponse = authenticationService.handleOAuth2Login(
                providerName, providerUserId, email, fullName
        );
        String targetUrl = UriComponentsBuilder.fromUriString(targetRedirectUrl)
                .queryParam("accessToken", authResponse.accessToken())
                .queryParam("refreshToken", authResponse.refreshToken())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}