package com.gsvn.productservice.service;




import com.gsvn.productservice.model.internal.IntrospectRequest;
import com.gsvn.productservice.model.internal.IntrospectResponse;
import java.util.List;
import java.util.Set;




public interface AuthenticationService {

    IntrospectResponse introspect(IntrospectRequest introspectRequest) ;
    Set<String> getPermissionByListRole(List<Integer> roleIds);
    Set<String> getPermissionsBySingleRole(Integer roleId) ;

}
