package com.example.review_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ReviewExceptionHandler {

    @ExceptionHandler(NotPurchasedException.class)
    public ResponseEntity<Object> handleNotPurchased(NotPurchasedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "NOT_PURCHASED", "message", e.getMessage()));
    }

    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<Object> handleDuplicate(DuplicateReviewException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "DUPLICATE_REVIEW", "message", e.getMessage()));
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ReviewNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "REVIEW_NOT_FOUND", "message", e.getMessage()));
    }
}