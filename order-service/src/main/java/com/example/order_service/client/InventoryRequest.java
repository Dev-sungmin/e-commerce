package com.example.order_service.client;

public record InventoryRequest(String orderId, Long productId, Integer quantity) {
}