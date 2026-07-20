package com.gsvn.paymentservice.exc;


import lombok.Getter;

@Getter
public class DuplicateResourceException extends AppException {
    private final String fieldName;

    public DuplicateResourceException(ErrorCode errorCode, String fieldName) {
        super(errorCode);
        this.fieldName = fieldName;
    }
}