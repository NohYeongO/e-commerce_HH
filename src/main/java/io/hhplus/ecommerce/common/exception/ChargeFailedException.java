package io.hhplus.ecommerce.common.exception;

public class ChargeFailedException extends RuntimeException {

    private final ErrorCode errorCode;

    public ChargeFailedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
