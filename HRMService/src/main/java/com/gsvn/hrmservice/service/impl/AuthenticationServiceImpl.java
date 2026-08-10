package com.gsvn.hrmservice.service.impl;


import com.gsvn.hrmservice.client.AuthServiceFeignClient;
import com.gsvn.hrmservice.client.RoleServiceFeignClient;
import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.config.CustomAuthenticationToken;
import com.gsvn.hrmservice.exc.AppException;
import com.gsvn.hrmservice.exc.ErrorCode;
import com.gsvn.hrmservice.mapper.StaffMapper;
import com.gsvn.hrmservice.model.entity.Staff;
import com.gsvn.hrmservice.model.internal.IntrospectRequest;
import com.gsvn.hrmservice.model.internal.IntrospectResponse;
import com.gsvn.hrmservice.service.AuthenticationService;
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
public class AuthenticationServiceImpl implements AuthenticationService {
    AuthServiceFeignClient authServiceFeignClient;
    StaffMapper staffMapper;
    RoleServiceFeignClient roleServiceFeignClient;
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) {
        ApiResponse<IntrospectResponse> response = authServiceFeignClient.authenticate(introspectRequest);
        return response.result();
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
        ApiResponse<Set<String>> response = roleServiceFeignClient.getPermissionRoleInternal(roleId);
        if (response != null && response.result() != null) {
            return response.result();
        }
        return Collections.emptySet();
    }
    private Staff getStaffFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof CustomAuthenticationToken staffAuth) {
            Long staffId = staffAuth.getStaffId();
            if (staffId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);
            return staffMapper.findById(staffId)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    public Long getStaffIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof CustomAuthenticationToken staffAuth) {
            return staffAuth.getStaffId();
        }
        return null;
    }

}
