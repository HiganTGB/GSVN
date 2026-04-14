package com.gsvn.accountservice.mapper;

import com.gsvn.accountservice.model.dto.request.UserBaseRequest;
import com.gsvn.accountservice.model.dto.response.UserBaseResponse;
import com.gsvn.accountservice.model.entity.User;

import java.time.OffsetDateTime;

public class UserMapper {
    public static User toUserEntity(UserBaseRequest request, String encodedPassword, Boolean isStaff) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .email(request.getEmail())
                .userName(request.getUserName())
                .password(encodedPassword)
                .phone(request.getPhoneNumber())
                .isStaff(isStaff != null ? isStaff : false)
                .verifier(request.getVerifier() != null ? request.getVerifier() : false)
                .isActive(false)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    public static void updateUserInfo(User user, UserBaseRequest request) {

        if (request.getPhoneNumber() != null) {
            user.setPhone(request.getPhoneNumber());
        }

        user.setUpdatedAt(OffsetDateTime.now());
    }

    public static UserBaseResponse toUserBaseResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserBaseResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .phone(user.getPhone())
                .verifier(user.getVerifier())
                .isActive(user.getIsActive())
                .isStaff(user.getIsStaff())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}