package com.profitflow.core_app.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCodes errorCode;

    public AppException(ErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCodes errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}