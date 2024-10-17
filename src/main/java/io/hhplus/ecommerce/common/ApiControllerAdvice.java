package io.hhplus.ecommerce.common;

import io.hhplus.ecommerce.common.exception.product.ProductNotFoundException;
import io.hhplus.ecommerce.common.exception.user.UserChargeFailedException;
import io.hhplus.ecommerce.common.exception.user.UserNotFoundException;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(UserChargeFailedException.class)
    public ResponseEntity<ErrorMessage> handleUserChargeFailedException(UserChargeFailedException e) {
        return ResponseEntity.status(500).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleProductNotFoundException(ProductNotFoundException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErrorMessage(e.getMessage()));
    }

}
