package com.gsvn.accountservice.config;

import com.gsvn.accountservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final RoleService roleService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        Map<String, Object> claims = jwt.getClaims();

        // Check is staff or not
        Object roleClaim = claims.get("role");
        if (roleClaim != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleClaim.toString()));
        }

        Object roleIdsObj = claims.get("roleIds");
        if (roleIdsObj instanceof Collection<?> roleIds) {
            for (Object idObj : roleIds) {
                try {
                    int roleId = Integer.parseInt(idObj.toString());

                    var permissions = roleService.getRolePermissions(roleId);

                    if (permissions != null) {
                        permissions.stream()
                                .map(p -> new SimpleGrantedAuthority(p.name()))
                                .forEach(authorities::add);
                    }
                } catch (Exception e) {
                    return new CustomAuthenticationToken(jwt, authorities);
                }
            }
        }
        return new CustomAuthenticationToken(jwt, authorities);
    }
}