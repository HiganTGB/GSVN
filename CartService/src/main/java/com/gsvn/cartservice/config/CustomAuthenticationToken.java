package com.gsvn.cartservice.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

@Getter
public class CustomAuthenticationToken extends JwtAuthenticationToken {
    private final Long staffId;
    private final Long customerId;
    public CustomAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, authorities);
        this.staffId = jwt.getClaim("staffId") != null ? (Long) jwt.getClaim("staffId") : null;
        this.customerId = jwt.getClaim("customerId") != null ? (Long) jwt.getClaim("customerId") : null;
    }
}
