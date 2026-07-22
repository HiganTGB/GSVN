package com.gsvn.shipmentservice.config;


import com.gsvn.shipmentservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final AuthenticationService authenticationService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        Map<String, Object> claims = jwt.getClaims();

        Object roleClaim = claims.get("role");
        if (roleClaim != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleClaim));
        }
        Object roleIdsObj = claims.get("roleIds");
        if (roleIdsObj instanceof Collection<?> roleIds) {
            List<Integer> listIds = roleIds.stream()
                    .map(id -> Integer.parseInt(id.toString()))
                    .toList();
            Set<String> permissions = authenticationService.getPermissionByListRole(listIds);

            if (permissions != null) {
                permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
            }
        }
        return new CustomAuthenticationToken(jwt, authorities);
    }
}