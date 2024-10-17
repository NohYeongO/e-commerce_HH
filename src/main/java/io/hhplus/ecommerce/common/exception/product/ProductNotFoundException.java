package io.hhplus.ecommerce.common.exception.product;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {
    int status;
    public ProductNotFoundException(String message, int status) {
        super(message);
        this.status = status;
    }
}
