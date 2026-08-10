package com.gsvn.hrmservice.common;

import com.gsvn.hrmservice.mapper.StaffMapper;
import com.gsvn.hrmservice.model.entity.Staff;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    public static Optional<String> getCurrentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(auth -> {
                    if (auth.getPrincipal() instanceof Jwt jwt) {
                        return jwt.getClaimAsString("userId");
                    }
                    return auth.getName();
                });
    }

}