package com.gsvn.orderservice.service;





import com.gsvn.orderservice.model.internal.IntrospectRequest;
import com.gsvn.orderservice.model.internal.IntrospectResponse;
import java.util.List;
import java.util.Set;



public interface AuthenticationService {

    IntrospectResponse introspect(IntrospectRequest introspectRequest) ;
    Set<String> getPermissionByListRole(List<Integer> roleIds) ;
    Set<String> getPermissionsBySingleRole(Integer roleId) ;
    Long getStaffIdFromToken() ;
    Long getCustomerIdFromToken() ;
}
