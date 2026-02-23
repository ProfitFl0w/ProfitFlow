package com.profitflow.core_app.exception;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    MERCHANT_ALREADY_EXISTS("USER_001", "Merchant with this email already exists", HttpStatus.CONFLICT),
    MERCHANT_NOT_FOUND("USER_002", "Merchant not found", HttpStatus.NOT_FOUND),

    INVALID_CREDENTIALS("AUTH_001", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("AUTH_002", "Token has expired", HttpStatus.UNAUTHORIZED),

    VALIDATION_ERROR("VAL_001", "Validation failed", HttpStatus.BAD_REQUEST),

    INVALID_MONEY_AMOUNT("MONEY_001", "Invalid money amount", HttpStatus.BAD_REQUEST),
    INVALID_CURRENCY_CODE("MONEY_002", "Invalid currency code", HttpStatus.BAD_REQUEST),
    CURRENCY_MISMATCH("MONEY_003", "Currency mismatch", HttpStatus.BAD_REQUEST),

    INTERNAL_SERVER_ERROR("SYS_001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}