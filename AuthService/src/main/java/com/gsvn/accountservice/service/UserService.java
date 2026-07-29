package com.gsvn.accountservice.service;

import com.gsvn.accountservice.common.PageResponse;
import com.gsvn.accountservice.model.dto.request.ChangePasswordRequest;
import com.gsvn.accountservice.model.dto.request.RegisterRequest;
import com.gsvn.accountservice.model.dto.request.SyncUserRequest;
import com.gsvn.accountservice.model.dto.request.UserBaseRequest;
import com.gsvn.accountservice.model.dto.response.UserBaseResponse;

public interface UserService {
    UserBaseResponse getMyInfo();
    void changeLockUser(String userId, boolean isActive);
    UserBaseResponse create(UserBaseRequest request, boolean isStaff);
    UserBaseResponse syncUser(String userId, SyncUserRequest request);
    void registerCustomer(RegisterRequest request);
    boolean changePassword(ChangePasswordRequest request);
    PageResponse<UserBaseResponse> getUsers(String keyword, Boolean isStaff, String sortBy, String direction, int page, int size);
    UserBaseResponse getById(String id);
}
