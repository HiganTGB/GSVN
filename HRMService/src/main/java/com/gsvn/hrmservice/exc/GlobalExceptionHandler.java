package com.gsvn.hrmservice.exc;


import com.gsvn.hrmservice.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handlingValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(ErrorCode.INVALID_REQUEST_BODY.getCode(),ErrorCode.INVALID_REQUEST_BODY.getMessage(),errors));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<?>> handlingDuplicateResourceException(DuplicateResourceException exception) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(exception.getErrorCode().getCode(),exception.getMessage(), Map.of(exception.getFieldName(), exception.getMessage())));
    }
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<String>> handlingAppException(AppException exception) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(exception.getErrorCode().getCode(),exception.getMessage()));
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ApiResponse<?>> handlingAuthenticationServiceException(AuthenticationServiceException exception) {
        log.error("System Error: ", exception);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ErrorCode.UNAUTHENTICATED.getCode(),"Token invalid"));
    }
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> handlingJwtException(JwtException exception) {
        log.error("System Error: ", exception);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ErrorCode.UNAUTHENTICATED.getCode(),"Token invalid"));
    }

    @ExceptionHandler(RuntimeException.class)
    public  ResponseEntity<ApiResponse<?>> handlingRuntimeException(RuntimeException exception) {
        log.error("System Error: ", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode(),ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage()));
    }


}