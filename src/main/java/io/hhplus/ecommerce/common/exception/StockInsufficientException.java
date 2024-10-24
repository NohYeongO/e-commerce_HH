package io.hhplus.ecommerce.common.exception;

import lombok.Getter;

@Getter
public class StockInsufficientException extends RuntimeException {
    private final ErrorCode errorCode;

    public StockInsufficientException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
