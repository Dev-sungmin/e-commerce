package com.example.order_service.dto;

public record OrderItemRequest(Long productId, Integer quantity) {
}