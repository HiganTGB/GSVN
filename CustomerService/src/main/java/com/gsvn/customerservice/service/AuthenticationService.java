package com.gsvn.customerservice.service;





import com.gsvn.customerservice.client.AuthServiceFeignClient;
import com.gsvn.customerservice.client.RoleServiceFeignClient;
import com.gsvn.customerservice.common.ApiResponse;
import com.gsvn.customerservice.config.CustomAuthenticationToken;
import com.gsvn.customerservice.model.internal.IntrospectRequest;
import com.gsvn.customerservice.model.internal.IntrospectResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticationService {
    AuthServiceFeignClient authServiceFeignClient;
    RoleServiceFeignClient roleServiceFeignClient;
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) {
        try {
            ApiResponse<IntrospectResponse> response = authServiceFeignClient.authenticate(introspectRequest);
            return response.result();
        } catch (Exception e) {
            log.info(e.getMessage());
            return new IntrospectResponse(false);
        }
    }
    public Set<String> getPermissionByListRole(List<Integer> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return Collections.emptySet();

        return roleIds.stream()
                .map(this::getPermissionsBySingleRole)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
    @Cacheable(value = "role_permissions", key = "#roleId", unless = "#result == null")
    public Set<String> getPermissionsBySingleRole(Integer roleId) {
        try {
            ApiResponse<Set<String>> response = roleServiceFeignClient.getPermissionRoleInternal(roleId);

            if (response != null && response.result() != null) {
                return response.result();
            }
        } catch (Exception e) {
           return Collections.emptySet();
        }
        return Collections.emptySet();
    }

    public Long getStaffIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof CustomAuthenticationToken staffAuth) {
            return staffAuth.getStaffId();
        }
        return null;
    }

    public Long getCustomerIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof CustomAuthenticationToken staffAuth) {
            return staffAuth.getCustomerId();
        }
        return null;
    }
}
