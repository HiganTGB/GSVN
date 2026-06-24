package com.gsvn.hrmservice.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.gsvn.hrmservice.model.dto.response.StaffResponse;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(int code, String message, T result) {

    public ApiResponse(String message, T result) {
        this(1000, message, result);
    }
    public ApiResponse(int code,String message) {
        this(code, message, null);
    }
    public ApiResponse(T result) {
        this(200,"Success", result);
    }
    public ApiResponse() {
        this(200,"Success");
    }
}