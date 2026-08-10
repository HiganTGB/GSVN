package com.gsvn.promotionservice.service;

import com.gsvn.promotionservice.model.dto.internal.IntrospectRequest;
import com.gsvn.promotionservice.model.dto.internal.IntrospectResponse;
import java.util.List;
import java.util.Set;



public interface AuthenticationService {

    IntrospectResponse introspect(IntrospectRequest introspectRequest);
    Set<String> getPermissionByListRole(List<Integer> roleIds);
    Set<String> getPermissionsBySingleRole(Integer roleId);


}
