package com.gsvn.mediaservice.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
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