package com.example.payment_service.exception;

public class TossApiException extends RuntimeException {
    public TossApiException(String message) {
        super(message);
    }
}