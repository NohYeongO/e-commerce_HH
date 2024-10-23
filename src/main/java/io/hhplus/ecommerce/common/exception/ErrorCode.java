package io.hhplus.ecommerce.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND(404, "회원이 존재 하지 않습니다"),
    PRODUCT_NOT_FOUND(404, "상품이 존재 하지 않습니다."),
    CHARGE_FAILED(500,"충전 중 오류가 발생했습니다"),
    DATA_INTEGRITY_VIOLATION(409, "무결성 제약 위반");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
