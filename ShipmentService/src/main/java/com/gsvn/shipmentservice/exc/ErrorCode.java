package com.gsvn.shipmentservice.exc;

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
    CONFLICT_VERSION(1054,"conflict version",HttpStatus.CONFLICT),
    SKU_EXISTED(1054,"SKU existed and linked to variant",HttpStatus.CONFLICT),
    SHIPMENT_NOT_FOUND(1060, "Shipment not found", HttpStatus.NOT_FOUND),
    INVALID_STATUS_TRANSITION(1061, "Invalid status transition", HttpStatus.BAD_REQUEST),
    LIMIT_ONE_OPTION(1054,"Only one options to sync skus",HttpStatus.CONFLICT);
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}