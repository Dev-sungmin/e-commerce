package com.example.order_service.dto;

public record OrderItemRequest(Long productId, String productName, Integer quantity, Integer price) {
}