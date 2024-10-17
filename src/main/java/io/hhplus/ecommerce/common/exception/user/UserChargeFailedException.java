package io.hhplus.ecommerce.common.exception.user;

public class UserChargeFailedException extends RuntimeException {
    public UserChargeFailedException(String message) {
        super(message);
    }
}
