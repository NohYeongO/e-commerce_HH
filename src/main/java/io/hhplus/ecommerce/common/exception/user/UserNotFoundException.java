package io.hhplus.ecommerce.common.exception.user;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    int status;
    public UserNotFoundException(String message, int status) {
        super(message);
        this.status = status;
    }
}
