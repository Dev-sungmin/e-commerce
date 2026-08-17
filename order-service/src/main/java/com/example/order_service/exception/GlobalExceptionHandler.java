package com.example.order_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStock(InsufficientStockException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "INSUFFICIENT_STOCK", "productId", e.getProductId()));
    }

    @ExceptionHandler(InventoryServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleInventoryUnavailable(InventoryServiceUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "INVENTORY_SERVICE_UNAVAILABLE"));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFound(OrderNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "ORDER_NOT_FOUND", "message", e.getMessage()));
    }

    @ExceptionHandler(OrderAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleOrderAccessDenied(OrderAccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "ORDER_ACCESS_DENIED", "message", e.getMessage()));
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidOrderState(InvalidOrderStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "INVALID_ORDER_STATE", "message", e.getMessage()));
    }
}