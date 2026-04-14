package com.gsvn.accountservice.service;


import com.gsvn.accountservice.client.CustomerServiceClient;
import com.gsvn.accountservice.exc.AppException;
import com.gsvn.accountservice.exc.DuplicateResourceException;
import com.gsvn.accountservice.exc.ErrorCode;
import com.gsvn.accountservice.mapper.UserMapper;

import com.gsvn.accountservice.model.dto.request.AuthenticationRequest;
import com.gsvn.accountservice.model.dto.request.RegisterRequest;
import com.gsvn.accountservice.model.dto.request.SyncUserRequest;
import com.gsvn.accountservice.model.dto.request.UserBaseRequest;
import com.gsvn.accountservice.model.dto.response.UserBaseResponse;

import com.gsvn.accountservice.model.entity.User;

import com.gsvn.accountservice.model.internal.CustomerRequest;
import com.gsvn.accountservice.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;



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
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return UserMapper.toUserBaseResponse(user);
    }
    public void changeLockUser(String user_id,boolean value){
        User user =userRepository.findById(user_id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setIsActive(value);
        userRepository.save(user);
    }
    @Transactional
    public UserBaseResponse create(UserBaseRequest request,boolean is_staff)
    {
        User user=UserMapper.toUserEntity(request,passwordEncoder.encode(request.getPassword()),is_staff);
        user= userRepository.save(user);
        return UserMapper.toUserBaseResponse(user);
    }
    @Transactional
    public UserBaseResponse syncUser(String userId, SyncUserRequest request)
    {
        var user=userRepository.findById(userId).orElseThrow(()->new AppException(ErrorCode.ITEM_NOT_EXISTED));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhoneNumber());
        user= userRepository.save(user);
        return UserMapper.toUserBaseResponse(user);
    }
    @Transactional
    public void registerCustomer(RegisterRequest request)
    {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if(user!=null)
        {
            user=UserMapper.toUserEntity(new UserBaseRequest(request.getEmail(),request.getFullName(),request.getPassword(),request.getPhoneNumber(),false),passwordEncoder.encode(request.getPassword()),false);
            user= userRepository.save(user);
            customerServiceClient.createInternalCustomer(new CustomerRequest(request.getFullName(),request.getEmail(),request.getPhoneNumber(),request.getGender(),request.getDob()),user.getUserId());
        }else
        {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY,"email");
        }
    }

}