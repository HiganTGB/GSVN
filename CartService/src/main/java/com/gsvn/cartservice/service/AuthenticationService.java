package com.gsvn.cartservice.service;

import com.gsvn.cartservice.model.internal.IntrospectRequest;
import com.gsvn.cartservice.model.internal.IntrospectResponse;

public interface AuthenticationService {
    IntrospectResponse introspect(IntrospectRequest introspectRequest);
    Long getCustomerIdFromToken();
}
