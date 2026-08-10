package com.gsvn.searchservice.exc;

import lombok.Getter;

@Getter
public class InvalidResourceException extends AppException {
    private final String fieldName;

    public InvalidResourceException(ErrorCode errorCode, String fieldName) {
        super(errorCode);
        this.fieldName = fieldName;
    }

}