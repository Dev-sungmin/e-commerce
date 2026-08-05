package com.example.order_service.exception;

public class InsufficientStockException extends RuntimeException {
    private final Long productId;

    public InsufficientStockException(Long productId, String message) {
        super(message);
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}