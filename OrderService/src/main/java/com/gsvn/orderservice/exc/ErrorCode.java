package com.gsvn.orderservice.exc;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    USER_LOCKED(1007, "User locked", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(1008, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1009, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST_BODY(1010,"Request body is missing or invalid",HttpStatus.BAD_REQUEST),
    ITEM_NOT_EXISTED(1052,"Item not existed",HttpStatus.BAD_REQUEST),
    NOT_ALLOW(1052,"Item not allow",HttpStatus.BAD_REQUEST),
    EXCESSIVE_CHECKIN(1052, "Excessive check-in attempts for today", HttpStatus.BAD_REQUEST),
    INVALID_LOCATION(1053, "Invalid check-in location", HttpStatus.BAD_REQUEST),
    MISSING_EMAIL(1055,"Missing Email",HttpStatus.BAD_REQUEST);
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}