package io.hhplus.ecommerce.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),
    PRODUCT_INCOMPLETE_RESULTS(HttpStatus.NOT_FOUND, "주문할 수 없는 상품이 존재합니다."),
    PRODUCT_INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "재고가 부족합니다."),
    CHARGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "충전 중 오류가 발생했습니다."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류 발생"),
    ORDER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "주문에 실패했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    POINTS_INSUFFICIENT(HttpStatus.BAD_REQUEST, "차감할 포인트가 부족합니다."),
    POINT_DEDUCTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "포인트 차감중 오류 발생"),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니에 상품이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
