package com.tgb.gsvnbackend.exc;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid request format. Please check your input.");
        return ResponseEntity.badRequest().body(errorResponse);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotfoundException(NotFoundException ex) {

        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(DataViolationException.class)
    public ResponseEntity<?> handleDataViolationException(DataViolationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
