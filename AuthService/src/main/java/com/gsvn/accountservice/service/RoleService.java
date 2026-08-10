package com.gsvn.accountservice.service;

import com.gsvn.accountservice.common.PageResponse;
import com.gsvn.accountservice.model.dto.request.RoleRequest;
import com.gsvn.accountservice.model.dto.response.PermissionResponse;
import com.gsvn.accountservice.model.dto.response.RoleResponse;
import com.gsvn.accountservice.model.entity.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {
    List<Role> getAll();
    Set<PermissionResponse> getRolePermissions(int id);
    Set<String> getRolePermissionStrings(Integer roleId);
    RoleResponse create(RoleRequest request);
    RoleResponse update(RoleRequest request, int id);
    void delete(int id);
    List<RoleResponse> getRoles();
    Set<RoleResponse> getRolesByUserId(String userId);
    Set<RoleResponse> updateRolesByUserId(String userId, List<Integer> newRoleIds);
    String getMyRoles();
    Set<String> getPermissionsByUserId(String userId);
    Set<String> getMyPermissions();
    public PageResponse<RoleResponse> searchRoles(String keyword, String sortBy, String direction, int page, int size);
    void warmUpCache();
}
