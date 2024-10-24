package io.hhplus.ecommerce.common.exception;

import lombok.Getter;

@Getter
public class OrderFailedException extends RuntimeException {
    private final ErrorCode errorCode;

    public OrderFailedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}