package io.hhplus.ecommerce.common;

import io.hhplus.ecommerce.common.exception.*;
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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(OrderFailedException.class)
    public ResponseEntity<ErrorMessage> handleOrderFailedException(OrderFailedException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(PointInsufficientException.class)
    public ResponseEntity<ErrorMessage> handlePointInsufficientException(PointInsufficientException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(StockInsufficientException.class)
    public ResponseEntity<ErrorMessage> handleStockInsufficientException(StockInsufficientException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(ChargeFailedException.class)
    public ResponseEntity<ErrorMessage> handleChargeFailedException(ChargeFailedException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(CartOperationException.class)
    public ResponseEntity<ErrorMessage> handleCartOperationException(CartOperationException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(new ErrorMessage(e.getMessage()));
    }

}
