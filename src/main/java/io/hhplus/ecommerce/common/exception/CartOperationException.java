package io.hhplus.ecommerce.common.exception;

import lombok.Getter;

@Getter
public class CartOperationException extends RuntimeException {
    private ErrorCode errorCode;
    public CartOperationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
