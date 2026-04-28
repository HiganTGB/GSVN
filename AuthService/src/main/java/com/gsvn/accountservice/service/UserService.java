package com.gsvn.accountservice.service;


import com.gsvn.accountservice.client.CustomerServiceClient;
import com.gsvn.accountservice.common.PageResponse;
import com.gsvn.accountservice.exc.AppException;
import com.gsvn.accountservice.exc.DuplicateResourceException;
import com.gsvn.accountservice.exc.ErrorCode;
import com.gsvn.accountservice.mapper.UserMapper;

import com.gsvn.accountservice.model.dto.request.*;
import com.gsvn.accountservice.model.dto.response.UserBaseResponse;

import com.gsvn.accountservice.model.entity.User;

import com.gsvn.accountservice.model.internal.CustomerRequest;
import com.gsvn.accountservice.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    CustomerServiceClient customerServiceClient;
    public UserBaseResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        return UserMapper.toUserBaseResponse(user);
    }
    public void changeLockUser(String userId,boolean isActive){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setIsActive(isActive);
        userRepository.save(user);
    }
    @Transactional
    public UserBaseResponse create(UserBaseRequest request,boolean isStaff)
    {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY, "email");
        }
        User user = UserMapper.toUserEntity(request, passwordEncoder.encode(request.getPassword()), isStaff);
        return UserMapper.toUserBaseResponse(userRepository.save(user));
    }
    @Transactional
    public UserBaseResponse syncUser(String userId, SyncUserRequest request)
    {
        var user=userRepository.findById(userId).orElseThrow(()->new AppException(ErrorCode.ITEM_NOT_EXISTED));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhoneNumber());
        if(user.getReferenceId()==null)
        {
            user.setReferenceId(request.getReferenceId());
        }
        user= userRepository.save(user);
        return UserMapper.toUserBaseResponse(user);
    }
    @Transactional
    public void registerCustomer(RegisterRequest request)
    {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY, "email");
        }
        User user = UserMapper.toUserEntity(
                new UserBaseRequest(request.getEmail(), request.getFullName(), request.getPassword(), request.getPhoneNumber(), false),
                passwordEncoder.encode(request.getPassword()),
                false
        );
        user = userRepository.save(user);
        var customerReq = new CustomerRequest(
                request.getFullName(), request.getEmail(), request.getPhoneNumber(),
                request.getGender(), request.getDob()
        );
        var response = customerServiceClient.createInternalCustomer(customerReq, user.getUserId());
        user.setReferenceId(response.result().getCustomerId());
        userRepository.save(user);
    }
    @Transactional
    public boolean changePassword(ChangePasswordRequest request)
    {
        if(!request.newPassword().equals(request.rePassword()))
        {
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY);
        }
        User user = getCurrentUser();
        if(!passwordEncoder.matches(request.oldPassword(),user.getPassword()))
        {
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY);
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return true;
    }


    public PageResponse<UserBaseResponse> getUsers(String keyword, Boolean isStaff, String sortBy, String direction, int page, int size) {
        String sortField = getValidSortField(sortBy);
        String sortOrder = "asc".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        Pageable pageable = PageRequest.of(page - 1, size);
        String searchKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        Page<User> userPage = userRepository.searchUsersNative(searchKeyword, isStaff, sortField, sortOrder, pageable);

        List<UserBaseResponse> content = userPage.getContent().stream()
                .map(UserMapper::toUserBaseResponse)
                .toList();

        return PageResponse.of(content, userPage.getTotalElements(), page, size);
    }
    public UserBaseResponse getById(String id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));

        return UserMapper.toUserBaseResponse(user);
    }
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }
    private String getValidSortField(String sortBy) {
        if (sortBy == null) return "createdAt";
        return switch (sortBy) {
            case "username" -> "userName";
            case "email" -> "email";
            case "phone" -> "phone";
            default -> "createdAt";
        };
    }
}