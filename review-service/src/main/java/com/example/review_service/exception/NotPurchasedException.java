package com.example.review_service.exception;

public class NotPurchasedException extends RuntimeException {
    public NotPurchasedException(String message) {
        super(message);
    }
}