package com.gsvn.customerservice.service;

import com.gsvn.customerservice.model.internal.IntrospectRequest;
import com.gsvn.customerservice.model.internal.IntrospectResponse;

import java.util.List;
import java.util.Set;

public interface AuthenticationService {
    IntrospectResponse introspect(IntrospectRequest introspectRequest);
    Set<String> getPermissionByListRole(List<Integer> roleIds);
    Set<String> getPermissionsBySingleRole(Integer roleId);
    Long getCustomerIdFromToken();
}
