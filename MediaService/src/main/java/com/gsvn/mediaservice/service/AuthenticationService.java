package com.gsvn.mediaservice.service;

import com.gsvn.mediaservice.common.IntrospectRequest;
import com.gsvn.mediaservice.common.IntrospectResponse;

public interface AuthenticationService {
    IntrospectResponse introspect(IntrospectRequest introspectRequest);

}
