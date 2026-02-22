package com.profitflow.core_app.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppException(AppException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("code", ex.getErrorCode().getCode());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, ex.getErrorCode().getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("code", ErrorCodes.VALIDATION_ERROR.getCode());
        body.put("message", ErrorCodes.VALIDATION_ERROR.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        body.put("details", errors);

        return new ResponseEntity<>(body, ErrorCodes.VALIDATION_ERROR.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("code", ErrorCodes.INTERNAL_SERVER_ERROR.getCode());
        body.put("message", ErrorCodes.INTERNAL_SERVER_ERROR.getMessage());
        body.put("details", ex.getMessage()); // Для MVP норм, в проде лучше скрывать детали

        return new ResponseEntity<>(body, ErrorCodes.INTERNAL_SERVER_ERROR.getStatus());
    }
}