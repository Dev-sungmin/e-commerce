package com.example.cart_service.dto;

import java.util.List;

public record CartResponse(List<CartItemResponse> items, Integer totalQuantity) {

    public record CartItemResponse(Long productId, Integer quantity) {}
}