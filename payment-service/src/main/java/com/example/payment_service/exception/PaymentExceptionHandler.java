package com.example.payment_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(AmountMismatchException.class)
    public ResponseEntity<Object> handleAmountMismatch(AmountMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "AMOUNT_MISMATCH", "message", e.getMessage()));
    }

    @ExceptionHandler(TossApiException.class)
    public ResponseEntity<Object> handleTossApiException(TossApiException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "TOSS_API_ERROR", "message", e.getMessage()));
    }
}