package io.hhplus.ecommerce.common.exception;

import lombok.Getter;

@Getter
public class PointInsufficientException extends RuntimeException {
    private final ErrorCode errorCode;

    public PointInsufficientException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
