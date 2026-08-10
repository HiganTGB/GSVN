package com.gsvn.accountservice.service;

import com.gsvn.accountservice.model.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    List<PermissionResponse> getAll();
}
