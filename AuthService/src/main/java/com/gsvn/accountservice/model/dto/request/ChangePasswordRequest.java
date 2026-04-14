package com.gsvn.accountservice.model.dto.request;

public record ChangePasswordRequest (String oldPassword,String newPassword ,String rePassword){
}
